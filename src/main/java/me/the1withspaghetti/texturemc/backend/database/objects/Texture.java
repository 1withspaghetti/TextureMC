package me.the1withspaghetti.texturemc.backend.database.objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class Texture {
	
	@NotBlank(message="data cannot be null")
	@Pattern(regexp="^[-A-Za-z0-9+\\/=]{3,}$", message="Invalid data")
	public String img;
	
	public boolean equals(Texture obj) {
		return (
				this.img.equals(obj.img)
				);	
	}
}
