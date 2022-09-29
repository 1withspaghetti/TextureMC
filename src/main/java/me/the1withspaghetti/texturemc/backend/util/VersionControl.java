package me.the1withspaghetti.texturemc.backend.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import me.the1withspaghetti.texturemc.backend.database.objects.Texture;

public class VersionControl {
	
	public static HashMap<String, Document> versionData = new HashMap<>();
	public static HashMap<String, Integer> formats = new HashMap<>();
	
	private static final String[] VERSIONS = {
			/*"1.8.9","1",
			"1.9.3","2",
			"1.10.2","2",
			"1.11.2","3",
			"1.12.2","3",
			"1.13.2","4",
			"1.14.4","4",
			"1.15.2","5",
			"1.16.5","6",
			"1.17.1","7",
			"1.18.1","8"*/
			"1.19.1","9",
			"1.19.2","9"
	};
	
	public static void init() throws IOException {
		
		for (int i = 0; i < VERSIONS.length; i+=2) {
			formats.put(VERSIONS[i], Integer.parseInt(VERSIONS[i+1]));
			
			String json = new String(VersionControl.class.getClassLoader().getResourceAsStream("assets/"+VERSIONS[i]+".json").readAllBytes());
			Document data = Document.parse(json);
			
			versionData.put(VERSIONS[i], data);
		}
	}
	
	public static boolean isVersion(String version) {
		return versionData.containsKey(version);
	}
	
	public static int getFormat(String version) {
		return formats.get(version);
	}
	
	public static boolean isItem(String version, String path) {
		List<String> listPath = Arrays.asList(path.split("/"));
		String name = listPath.set(listPath.size() - 1, "files");
		return (versionData.get(version).getEmbedded(listPath, List.class).contains(name));
	}
	
	public static Texture getItem(String version, String path) {
		return versionData.get(version).getEmbedded(Arrays.asList(path.split("/")), Texture.class);
	}
	
	/*public static int getFormat(String version) {
		return formats.get(version);
	}*/
	
	/*private static String printSet(Set<String> set) {
		StringBuilder str = new StringBuilder("[");
		for (String s: set)
			str.append(s+",");
		//str.deleteCharAt(str.length()-1);
		str.append("]");
		return str.toString();
	}*/
}
