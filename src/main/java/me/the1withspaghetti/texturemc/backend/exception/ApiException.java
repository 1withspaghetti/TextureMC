package me.the1withspaghetti.texturemc.backend.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
	
	private static final long serialVersionUID = 3588038711918866523L;
	
	public HttpStatus status;

	public ApiException() {
		super();
		this.status = HttpStatus.BAD_REQUEST;
	}
	
	public ApiException(String msg) {
		super(msg);
		this.status = HttpStatus.BAD_REQUEST;
	}
	
	public ApiException(HttpStatus status) {
		super();
		this.status = status;
	}
	
	public ApiException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}
}
