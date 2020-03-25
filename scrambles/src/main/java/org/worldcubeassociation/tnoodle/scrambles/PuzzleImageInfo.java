package org.worldcubeassociation.tnoodle.scrambles;

import java.util.HashMap;

import org.worldcubeassociation.tnoodle.svglite.Color;
import org.worldcubeassociation.tnoodle.svglite.Dimension;

public class PuzzleImageInfo {
	public HashMap<String, Color> colorScheme;
	public Dimension size;

	public PuzzleImageInfo() {
	}

	public PuzzleImageInfo(Puzzle p) {
		colorScheme = p.getDefaultColorScheme();
		size = p.getPreferredSize();
	}

	public HashMap<String, Object> toJsonable() {
		HashMap<String, Object> jsonable = new HashMap<String, Object>();
		HashMap<String, Integer> dim = new HashMap<String, Integer>();
		dim.put("width", size.getWidth());
		dim.put("height", size.getHeight());
		jsonable.put("size", dim);

		HashMap<String, String> jsonColorScheme = new HashMap<String, String>();
		for (String key : colorScheme.keySet()) {
			jsonColorScheme.put(key, colorScheme.get(key).toHex());
		}
		jsonable.put("colorScheme", jsonColorScheme);

		return jsonable;
	}
}
