package me.the1withspaghetti.texturemc.backend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encryption {
	
	public static String SHA_256(String str) throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
		return toBase64(hash);
	}
	
	public static String toBase64(byte[] bytes) {
		return new String(Base64.getEncoder().encode(bytes));
	}
}
