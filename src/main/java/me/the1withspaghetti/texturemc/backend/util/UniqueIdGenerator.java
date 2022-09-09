package me.the1withspaghetti.texturemc.backend.util;

import org.springframework.http.HttpStatus;

import me.the1withspaghetti.texturemc.backend.exception.ApiException;

public class UniqueIdGenerator {

	private long last = 0;
	private long i = 0;
	
	public synchronized long generateNewId() {
		long t = System.currentTimeMillis();
		if (t == last) {
			// If another id has been generated in the same millisecond, increment i
			i++;
			if (i > 255) {
				// If 255 ids have already been generated, throw an exception
				throw new ApiException("Too many users creating accounts! Try again later.", HttpStatus.TOO_MANY_REQUESTS);
			}
		} else {
			// Reset i to 0
			i = 0;
		}
		// Shift bits to left, making room for increment string
		t = t << 8;
		// Insert increment string in the first 8 bits
		t = t | (i & 111111111);
		
		return t;
	}
}
