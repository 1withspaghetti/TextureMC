package me.the1withspaghetti.texturemc.backend.endpoints;

import java.sql.SQLException;

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
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.PackListResponse;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.Response;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.BasicPackRequest;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.CreatePackRequest;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.RenamePackRequest;
import me.the1withspaghetti.texturemc.backend.exception.ApiException;
import me.the1withspaghetti.texturemc.backend.service.SessionService;
import me.the1withspaghetti.texturemc.backend.service.SessionService.SessionData;

@RestController
@RequestMapping("/packs")
@CrossOrigin(origins = "*", methods= {RequestMethod.POST, RequestMethod.GET}, allowedHeaders= {"x-session-token","content-type"})
public class Packs {
	
	@GetMapping("/list")
	public static Response listPacks(@CookieValue(value = "session_token", defaultValue = "") String token) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		return new PackListResponse(AccountDB.getPacks(session.userId));
	}
	
	@PostMapping("/create")
	public static Response create(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody CreatePackRequest req) {
		
		
		return new Response(false);
	}
	
	@PostMapping("/rename")
	public static Response rename(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody RenamePackRequest req) {
		
		
		return new Response(false);
	}
	
	@PostMapping("/duplicate")
	public static Response duplicate(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody BasicPackRequest req) {
		
		
		return new Response(false);
	}
	
	@PostMapping("/delete")
	public static Response delete(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody BasicPackRequest req) {
		
		
		return new Response(false);
	}
	
}