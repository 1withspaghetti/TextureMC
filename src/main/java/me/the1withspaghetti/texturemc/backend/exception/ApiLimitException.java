package me.the1withspaghetti.texturemc.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ApiLimitException extends RuntimeException {
	private static final long serialVersionUID = -8697122772894407871L;
	// Status is managed with ResponseStatus annotation
}

