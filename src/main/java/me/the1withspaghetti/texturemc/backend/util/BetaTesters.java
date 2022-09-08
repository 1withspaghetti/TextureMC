package me.the1withspaghetti.texturemc.backend.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BetaTesters {
	
	private static List<String> keys = new ArrayList<>();
	
	public static void addKey(String key) throws IOException {
		keys.add(key);
	}
	
	public static void listKeys() {
		for (String key : keys) {
			System.out.println(key);
		}
	}
	
	public static void removeKey(String key) throws IOException {
		keys.remove(key);
	}
	
	public static boolean useKey(String key) {
		if (keys.contains(key)) {
			keys.remove(key);
			return true;
		}
		return false;
	}
}
