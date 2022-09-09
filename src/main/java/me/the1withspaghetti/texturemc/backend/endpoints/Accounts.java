package me.the1withspaghetti.texturemc.backend.endpoints;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.Response;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.RegisterRequest;
import me.the1withspaghetti.texturemc.backend.exception.ApiException;
import me.the1withspaghetti.texturemc.backend.util.Encryption;
import me.the1withspaghetti.texturemc.backend.util.UniqueIdGenerator;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", methods= {RequestMethod.POST, RequestMethod.GET})
public class Accounts {
	
	UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();
	
	@PostMapping("/register")
	public Response register(@Validated @RequestBody RegisterRequest req) throws Exception {
		if (req.password.equals(req.password2)) throw new ApiException("Passwords must match");
		
		long id = uniqueIdGenerator.generateNewId();
		String hash = Encryption.SHA_256(req.password);
		// TODO Send email via sendgrid api
		
		
		return new Response(true);
	}
	
}