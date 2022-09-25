package me.the1withspaghetti.texturemc.backend.endpoints;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;

import javax.validation.constraints.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.the1withspaghetti.texturemc.backend.database.AccountDB;
import me.the1withspaghetti.texturemc.backend.database.AccountDB.Pack;
import me.the1withspaghetti.texturemc.backend.database.PackDB;
import me.the1withspaghetti.texturemc.backend.database.objects.Texture;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.PackListResponse;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.Response;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.TextureResponse;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.BasicPackRequest;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.CreatePackRequest;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.RenamePackRequest;
import me.the1withspaghetti.texturemc.backend.exception.ApiException;
import me.the1withspaghetti.texturemc.backend.service.SessionService;
import me.the1withspaghetti.texturemc.backend.service.SessionService.SessionData;
import me.the1withspaghetti.texturemc.backend.util.VersionControl;

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
		
		if (!VersionControl.isVersion(req.version)) throw new ApiException("Invalid Version");
		
		long id = rand.nextLong();
		
		AccountDB.addPack(id, session.userId, req.name, req.version);
		PackDB.createPack(id, session.userId);
		
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
		PackDB.duplicatePack(req.id, session.userId, newId);
		
		return new Response(false);
	}
	
	@PostMapping("/delete")
	public static Response delete(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody BasicPackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		if (!AccountDB.deletePack(req.id, session.userId)) throw new ApiException("Unknown Pack", HttpStatus.NOT_FOUND);
		PackDB.deletePack(req.id, session.userId);
		
		return new Response(false);
	}
	
	
	@PostMapping("/{id}/upload")
	public static Response upload(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody Texture req, @PathVariable(value="id") long pack, @RequestParam @Pattern(regexp="^[a-b\\/]{1,100}$") String path) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		String version = AccountDB.getPackVersion(pack, session.userId);
		if (version == null) throw new ApiException("Unknown Pack");
		if (!VersionControl.isItem(version, path)) throw new ApiException("Unknown item");
		
		if (PackDB.insertItem(pack, session.userId, path, req)) throw new ApiException("Unknown Pack");
		
		return new Response(true);
	}
	
	@PostMapping("/{id}/get")
	public static Response get(@CookieValue(value = "session_token", defaultValue = "") String token, @PathVariable(value="id") long pack, @RequestParam @Pattern(regexp="^[a-b\\/]{1,100}$") String path) {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		Texture item = PackDB.getItem(pack, session.userId, path);
		if (item == null) throw new ApiException("Unknown Item");
		
		return new TextureResponse(item);
	}
	
}