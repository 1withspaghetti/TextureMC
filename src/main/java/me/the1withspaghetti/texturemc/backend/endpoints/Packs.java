package me.the1withspaghetti.texturemc.backend.endpoints;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;

import org.springframework.http.HttpStatus;
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
import me.the1withspaghetti.texturemc.backend.database.AccountDB.Pack;
import me.the1withspaghetti.texturemc.backend.database.PackDB;
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
	
	public static final int MAX_PACKS = 5;
	
	static Random rand = new Random();
	
	@GetMapping("/list")
	public static Response listPacks(@CookieValue(value = "session_token", defaultValue = "") String token) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		LinkedList<Pack> list = AccountDB.getPacks(session.userId);
		return new PackListResponse(list, list.size() >= MAX_PACKS);
	}
	
	@PostMapping("/create")
	public static Response create(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody CreatePackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		// TODO: Validate version
		
		long id = rand.nextLong();
		
		AccountDB.addPack(id, session.userId, req.name, req.version);
		PackDB.createPack(id);
		
		return new Response(false);
	}
	
	@PostMapping("/rename")
	public static Response rename(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody RenamePackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		if (!AccountDB.renamePack(req.id, session.userId, req.name)) throw new ApiException("Unknown Pack", HttpStatus.NOT_FOUND);
		
		return new Response(false);
	}
	
	@PostMapping("/duplicate")
	public static Response duplicate(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody BasicPackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		long newId = rand.nextLong();
		
		if (!AccountDB.duplicatePack(req.id, session.userId, newId)) throw new ApiException("Unknown Pack", HttpStatus.NOT_FOUND);
		PackDB.duplicatePack(req.id, newId);
		
		return new Response(false);
	}
	
	@PostMapping("/delete")
	public static Response delete(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody BasicPackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		if (!AccountDB.deletePack(req.id, session.userId)) throw new ApiException("Unknown Pack", HttpStatus.NOT_FOUND);
		PackDB.deletePack(req.id);
		
		return new Response(false);
	}
	
}