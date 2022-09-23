package me.the1withspaghetti.texturemc.backend.endpoints.serverbound;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class RenameUserRequest {

	@NotBlank(message="Username cannot be null")
	@Pattern(regexp="\\w{2,16}", message="Username must be 2 - 16 characters and only contain letters, numbers, and underscores.")
	public String username;
}
