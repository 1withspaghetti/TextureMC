package me.the1withspaghetti.texturemc.backend.endpoints.clientbound;

import org.bson.Document;

public class FullPackResponse extends Response {

	public String version;
	public int format;
	public Document data;
	
	public FullPackResponse(String version, int format, Document data) {
		super(true);
		this.version = version;
		this.format = format;
		this.data = data;
	}
}
