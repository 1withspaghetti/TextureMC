package me.the1withspaghetti.texturemc.backend.endpoints.serverbound;

import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import me.the1withspaghetti.texturemc.backend.database.objects.Texture;

public class ImportPackRequest {
	
	@NotBlank(message="Name cannot be null")
	@Pattern(regexp="^[\\w !@#$%^&*()§\\-_+=~`<>?|\\/{}\\[\\]:]{1,45}$", message="Invalid Name")
	public String name;
	
	@NotNull(message="Name cannot be null")
	@Max(value=9, message="Invalid Version")
	@Min(value=1, message="Invalid Version")
	public int format;
	
	@NotNull(message="Data cannot be null")
	public Map<String, Texture> data;
}
