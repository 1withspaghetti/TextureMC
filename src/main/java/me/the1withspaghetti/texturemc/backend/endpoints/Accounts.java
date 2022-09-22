package me.the1withspaghetti.texturemc.backend.endpoints;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.the1withspaghetti.texturemc.backend.database.AccountDB;
import me.the1withspaghetti.texturemc.backend.database.AccountDB.User;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.Response;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.LoginRequest;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.RegisterRequest;
import me.the1withspaghetti.texturemc.backend.exception.ApiException;
import me.the1withspaghetti.texturemc.backend.service.MailService;
import me.the1withspaghetti.texturemc.backend.service.SessionService;
import me.the1withspaghetti.texturemc.backend.util.Encryption;
import me.the1withspaghetti.texturemc.backend.util.UniqueIdGenerator;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", methods= {RequestMethod.POST, RequestMethod.GET})
public class Accounts {
	
	UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();
	SecureRandom rand = new SecureRandom();
	
	@PostMapping("/register")
	public Response register(@Validated @RequestBody RegisterRequest req, HttpServletResponse res) throws Exception {
		if (!req.password.equals(req.password2)) throw new ApiException("Passwords must match");
		
		long id = uniqueIdGenerator.generateNewId();
		String hash = Encryption.SHA_256(req.password);
		
		long verifyId = rand.nextLong();
		MailService.sendConfirmationEmail(req.email, "https://texturemc.com/confirm-email/?confirmation="+verifyId);
		AccountDB.createVerificationRequest(verifyId, id, System.currentTimeMillis());
		
		AccountDB.addUser(id, req.email, req.username, hash);
		
		UUID session = SessionService.newSession(id);
		Cookie token = new Cookie("session_token", session.toString());
		token.setSecure(false);
		token.setPath("/");
		token.setMaxAge((int) TimeUnit.HOURS.toSeconds(1));
		res.addCookie(token);
		return new Response(true);
	}
	
	@PostMapping("/login")
	public Response login(@Validated @RequestBody LoginRequest req, HttpServletResponse res) throws Exception {
		String hash = Encryption.SHA_256(req.password);
		
		User user = AccountDB.getUser(req.email, hash);
		if (user == null) throw new ApiException("Invalid email or password.");
		
		UUID session = SessionService.newSession(user.id);
		Cookie token = new Cookie("session_token", session.toString());
		token.setSecure(false);
		token.setPath("/");
		token.setMaxAge((int) TimeUnit.HOURS.toSeconds(1));
		res.addCookie(token);
		return new Response(true);
	}
	
	@GetMapping("heartbeat")
	public Response heartbeat(@CookieValue(value = "session_token", defaultValue = "") String token) {
		UUID session = SessionService.getUUID(token);
		if (session == null) throw new ApiException("Invalid Session");
		SessionService.getSession(session).heartbeat();
		return new Response(true);
	}
	
}