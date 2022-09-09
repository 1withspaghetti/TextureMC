package me.the1withspaghetti.texturemc.backend.endpoints.clientbound;

public class ErrorResponse extends Response {
	
	public String reason;

	public ErrorResponse(String reason) {
		super(false);
		this.reason = reason;
	}

}
