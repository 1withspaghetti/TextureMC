package me.the1withspaghetti.texturemc.backend.util;

public class Checks {
	
	public static final String REGEX_USERNAME = "\\w{2,16}";
	public static final String REGEX_PASSWORD = "[\\w@#$%^&-+=()]{4,32}";
	public static final String REGEX_UUID = "[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}";
	public static final String REGEX_ASSET = "[\\w~]{2,64}";
	
	public static boolean username(String str) {
		if (str == null) return false;
		return str.matches(REGEX_USERNAME);
	}
	
	public static boolean password(String str) {
		if (str == null) return false;
		return str.matches(REGEX_PASSWORD);
	}
	
	public static boolean uuid(String str) {
		if (str == null) return false;
		return str.matches(REGEX_UUID);
	}
	
	public static boolean asset(String str) {
		if (str == null) return false;
		return str.matches(REGEX_ASSET);
	}

}
