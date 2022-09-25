package me.the1withspaghetti.texturemc.backend.endpoints.clientbound;

import me.the1withspaghetti.texturemc.backend.database.objects.Texture;

public class TextureResponse extends Response {
	
	public Texture data;

	public TextureResponse(Texture data) {
		super(true);
		this.data = data;
	}
}
