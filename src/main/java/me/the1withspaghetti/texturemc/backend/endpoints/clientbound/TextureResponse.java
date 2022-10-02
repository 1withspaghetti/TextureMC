package me.the1withspaghetti.texturemc.backend.endpoints.clientbound;

import org.bson.Document;

public class TextureResponse extends Response {
	
	public Document data;

	public TextureResponse(Document data) {
		super(true);
		this.data = data;
	}
}
