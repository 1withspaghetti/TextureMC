package me.the1withspaghetti.texturemc.backend.util;

import java.io.IOException;

public class VersionControl {
	
	/*
	 *   [Version Name] [type-list map]
	 *   					V
	 *   		 [Type Name] [List of Items]
	 */
	/*public static HashMap<String, HashMap<String, HashSet<String>>> versions = new HashMap<String, HashMap<String, HashSet<String>>>();
	public static HashMap<String, HashMap<String, HashMap<String, MetaData>>> defMeta = new HashMap<String, HashMap<String, HashMap<String, MetaData>>>();
	public static HashMap<String, Integer> formats = new HashMap<>();*/
	
	/*private static final String[] versionNames = {
			"1.8.9","1.9.3","1.10.2","1.11.2",
			"1.12.2","1.13.2","1.14.4","1.15.2",
			"1.16.5","1.17.1","1.18.1"};
	
	private static final String[] versionFormat = {
			"1.8.9","1",
			"1.9.3","2",
			"1.10.2","2",
			"1.11.2","3",
			"1.12.2","3",
			"1.13.2","4",
			"1.14.4","4",
			"1.15.2","5",
			"1.16.5","6",
			"1.17.1","7",
			"1.18.1","8"
	};*/
	
	public static void init() throws IOException {
		
		/*for (String n: versionNames) {
			InputStreamReader stream = new InputStreamReader(VersionControl.class.getClassLoader().getResourceAsStream("versions/"+n+".json"));
			JsonObject lists = PiskelManager.gson.fromJson(stream, JsonObject.class);
			HashMap<String, HashSet<String>> types = new HashMap<String, HashSet<String>>();
			
			for (Entry<String, JsonElement> s: lists.entrySet()) {
				HashSet<String> items = new HashSet<String>();
				s.getValue().getAsJsonArray().forEach((i) -> {
					items.add(i.getAsString());
				});
				types.put(s.getKey(), items);
			}
			versions.put(n, types);
			System.out.println("Finished loading version assets for "+n);
		}
		
		for (String n: versionNames) {
			InputStreamReader stream = new InputStreamReader(VersionControl.class.getClassLoader().getResourceAsStream("meta/"+n+".json"));
			JsonObject lists = PiskelManager.gson.fromJson(stream, JsonObject.class);
			HashMap<String, HashMap<String, MetaData>> types = new HashMap<String, HashMap<String, MetaData>>();
			
			//         Type      Item List
			for (Entry<String, JsonElement> typeList: lists.entrySet()) {
				//	  Type List     Item
				HashMap<String, MetaData> items = new HashMap<String, MetaData>();
				for (Entry<String, JsonElement> y: typeList.getValue().getAsJsonObject().entrySet()) {
					//System.out.println(y.getKey()+":  "+y.getValue());
					items.put(y.getKey(), new MetaData(y.getValue().getAsJsonObject()));
				}
				types.put(typeList.getKey(), items);
			}
			defMeta.put(n, types);
			System.out.println("Finished loading meta assets for "+n);
		}
		
		for (int i = 0; i < versionFormat.length; i += 2) {
			formats.put(versionFormat[i], Integer.parseInt(versionFormat[i+1]));
		}*/
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
