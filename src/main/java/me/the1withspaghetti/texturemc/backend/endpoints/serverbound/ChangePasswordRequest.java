package me.the1withspaghetti.texturemc.backend.endpoints.serverbound;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ChangePasswordRequest {

	@NotBlank(message="Password cannot be null")
	@Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,32}$", message="Invalid Password")
	public String oldPassword;
	
	@NotBlank(message="Password cannot be null")
	@Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,32}$", message="Invalid Password")
	public String newPassword;
}
