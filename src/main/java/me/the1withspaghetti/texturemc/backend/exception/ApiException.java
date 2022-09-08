package me.the1withspaghetti.texturemc.backend.exception;

public class ApiException extends Exception {
	
	private static final long serialVersionUID = 3588038711918866523L;

	public ApiException() {
		super();
	}
	
	public ApiException(String msg) {
		super(msg);
	}
}
