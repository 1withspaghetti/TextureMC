package me.the1withspaghetti.texturemc.backend.endpoints.serverbound;

import javax.validation.constraints.NotNull;

public class BasicPackRequest {

	@NotNull(message="Id cannot be null")
	public long id;
	
}
