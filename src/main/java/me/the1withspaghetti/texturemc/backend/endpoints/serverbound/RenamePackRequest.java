package me.the1withspaghetti.texturemc.backend.endpoints.serverbound;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class RenamePackRequest {
	
	@NotNull(message="Id cannot be null")
	public long id;

	@NotBlank(message="Name cannot be null")
	@Pattern(regexp="^[\\w !@#$%^&*()§\\-_+=~`<>?|\\/{}\\[\\]:]{1,45}$", message="Invalid Pack Name")
	public String name;
}
