package me.the1withspaghetti.texturemc.backend.endpoints.serverbound;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CreatePackRequest {
	
	@NotBlank(message="Name cannot be null")
	@Pattern(regexp="^[\\w !@#$%^&*()§\\-_+=~`<>?|\\/{}\\[\\]:]{1,45}$", message="Invalid Name")
	public String name;
	
	@NotBlank(message="Version cannot be null")
	@Pattern(regexp="^[0-9.]{3,8}$", message="Invalid Version")
	public String version;
}
