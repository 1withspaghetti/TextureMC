package me.the1withspaghetti.texturemc.backend.util;

import java.util.HashMap;

import me.the1withspaghetti.texturemc.backend.exception.ApiLimitException;

public class IpRateLimiter {
	
	private static HashMap<byte[], Integer> limits = new HashMap<>();
	
	public static final int MAX_REQUESTS_PER_PERIOD = 1000;
	
	public static void clearLimits() {
		limits.clear();
	}
	
	public static void checkIP(byte[] ip, int increment) {
		limits.compute(ip, (k, v) -> {
			if (v != null && v > MAX_REQUESTS_PER_PERIOD) throw new ApiLimitException();
			return (v == null) ? increment : v+increment;
		});
	}
	
	public static void checkIP(String ip, int increment) {
		checkIP(parseIP(ip), increment);
	}
	
	
	public static byte[] parseIP(String ipAddr) {
		byte[] ret = new byte[4];
		String[] ipArr = ipAddr.split("\\.");
		ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
		ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
		ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
		ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
		return ret;
	}
	
}
