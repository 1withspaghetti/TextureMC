package me.the1withspaghetti.texturemc.backend.exception;

import org.springframework.http.HttpStatus;

public class ApiLimitException extends ApiException {
	private static final long serialVersionUID = -8697122772894407871L;
	
	public ApiLimitException() {
		super("Too many requests! Slow down!", HttpStatus.TOO_MANY_REQUESTS);
	}
}

