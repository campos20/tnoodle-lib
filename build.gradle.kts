import configurations.Languages.attachLocalRepositories

import proguard.gradle.ProGuardTask

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(PROGUARD_GRADLE)
        classpath(GOOGLE_APPENGINE_GRADLE)
    }
}

allprojects {
    group = "org.worldcubeassociation.tnoodle"
    version = "0.15.0"

    attachLocalRepositories()
}

plugins {
    KOTLIN_JVM apply false
}

val releasePrefix = "TNoodle-WCA"

tasks.create("registerReleaseTag") {
    doFirst {
        project.ext.set("TNOODLE_IMPL", releasePrefix)
        project.ext.set("TNOODLE_VERSION", project.version)
    }
}

tasks.create("registerCloudReleaseTag") {
    dependsOn("registerReleaseTag")

    doFirst {
        project.ext.set("TNOODLE_IMPL", "TNoodle-CLOUD")
    }
}

tasks.create<ProGuardTask>("generateOfficialRelease") {
    description = "Generate an official WCA release artifact."
    group = "WCA"

    val targetProject = "webscrambles"

    val targetBuildDir = project(":$targetProject").buildDir
    val targetConfigurations = project(":$targetProject").configurations

    dependsOn(getTasksByName("publishToMavenLocal", true))
    dependsOn("registerReleaseTag", ":$targetProject:cleanShadowJar", ":$targetProject:shadowJar")

    injars("$targetBuildDir/libs/$targetProject-$version-all.jar")
    outjars("$releasePrefix-$version.jar")

    if (JavaVersion.current().isJava9Compatible) {
        libraryjars("${System.getProperty("java.home")}/jmods")
    } else {
        libraryjars("${System.getProperty("java.home")}/lib/rt.jar")
        libraryjars("${System.getProperty("java.home")}/lib/jce.jar")
    }

    libraryjars(targetConfigurations.findByName("runtimeClasspath")?.files)

    printmapping("$buildDir/minified/proguard.map")
    allowaccessmodification()
    dontskipnonpubliclibraryclassmembers()

    // FIXME...? Routes currently don't work in the browser when code gets obfuscated or optimised
    dontobfuscate()
    dontoptimize()

    // cf. https://github.com/ktorio/ktor-samples/tree/master/other/proguard
    keep("class org.worldcubeassociation.tnoodle.server.** { *; }")
    keep("class io.ktor.server.netty.Netty { *; }")
    keep("class kotlin.reflect.jvm.internal.** { *; }")
    keep("class kotlin.text.RegexOption { *; }")

    // CSS rendering uses reflection black magic, so static bytecode optimisers need a little help
    keep("class org.apache.batik.css.parser.** { *; }")
    keep("class org.apache.batik.dom.** { *; }")
    keep("class com.itextpdf.text.ImgTemplate { *; }")

    keep("class ch.qos.logback.core.FileAppender { *; }")

    keepclasseswithmembernames("""class * {
        native <methods>;
    }""".trimIndent())

    keepclasseswithmembernames("""enum * {
        <fields>;
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }""")

    dontwarn()
}

tasks.create<JavaExec>("startOfficialServer") {
    description = "Starts the TNoodle server from an official release artifact. Builds one if necessary."
    group = "WCA"

    dependsOn("generateOfficialRelease")

    main = "-jar"
    args = listOf("$releasePrefix-$version.jar")
}

tasks.create("generateDebugRelease") {
    dependsOn(":webscrambles:shadowJar")
}

tasks.create("deployToCloud") {
    dependsOn(getTasksByName("publishToMavenLocal", true))
    dependsOn("registerCloudReleaseTag", ":webscrambles:appengineDeploy")
}

tasks.create("emulateCloudLocal") {
    dependsOn(getTasksByName("publishToMavenLocal", true))
    dependsOn("registerCloudReleaseTag", ":webscrambles:appengineRun")
}
