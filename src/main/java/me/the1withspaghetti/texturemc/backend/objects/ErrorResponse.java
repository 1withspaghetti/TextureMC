package me.the1withspaghetti.texturemc.backend.objects;

public class ErrorResponse extends Response {
	
	public String reason;

	public ErrorResponse(String reason) {
		super(false);
		this.reason = reason;
	}

}
