package org.worldcubeassociation.tnoodle.svglite;

public class Svg extends Element {

	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String PX = "px";

	public Svg(Dimension size) {
		super("svg");
		setSize(size);
		setAttribute("version", "1.1");
		setAttribute("xmlns", "http://www.w3.org/2000/svg");
	}

	public void setSize(Dimension size) {
		setAttribute(WIDTH, "" + size.getWidth() + PX);
		setAttribute(HEIGHT, "" + size.getHeight() + PX);
		setAttribute("viewBox", "0 0 " + size.getWidth() + " " + size.getHeight());
	}

	public Dimension getSize() {
		int width = Integer.parseInt(getAttribute(WIDTH).replace(PX, ""));
		int height = Integer.parseInt(getAttribute(HEIGHT).replace(PX, ""));
		return new Dimension(width, height);
	}

}
