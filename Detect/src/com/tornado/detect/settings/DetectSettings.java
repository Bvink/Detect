package com.tornado.detect.settings;

public class DetectSettings {

	private static int difficulty = 0;
	private static boolean testing = true;
	private static boolean theworld = false;

	public static void setDifficulty(int i) {
		difficulty = i;
	}

	public static int getDifficulty() {
		return difficulty;
	}

	public static boolean getTesting() {
		return testing;
	}
	
	public static void setWorld(boolean b) {
		theworld = b;
	}

	public static boolean getWorld() {
		return theworld;
	}

}
