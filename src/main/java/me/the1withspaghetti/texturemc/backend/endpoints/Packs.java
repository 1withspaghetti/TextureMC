package me.the1withspaghetti.texturemc.backend.endpoints;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import javax.validation.constraints.Pattern;

import org.bson.Document;
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

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.the1withspaghetti.texturemc.backend.database.AccountDB;
import me.the1withspaghetti.texturemc.backend.database.AccountDB.Pack;
import me.the1withspaghetti.texturemc.backend.database.PackDB;
import me.the1withspaghetti.texturemc.backend.database.objects.PackData;
import me.the1withspaghetti.texturemc.backend.database.objects.Texture;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.FullPackResponse;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.PackListResponse;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.Response;
import me.the1withspaghetti.texturemc.backend.endpoints.clientbound.TextureResponse;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.BasicPackRequest;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.CreatePackRequest;
import me.the1withspaghetti.texturemc.backend.endpoints.serverbound.ImportPackRequest;
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
	static ObjectMapper mapper = new ObjectMapper();
	
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
		
		return new Response(true);
	}
	
	@PostMapping("/rename")
	public static Response rename(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody RenamePackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		if (!AccountDB.renamePack(req.id, session.userId, req.name)) throw new ApiException("Unknown Pack", HttpStatus.NOT_FOUND);
		
		return new Response(true);
	}
	
	@PostMapping("/duplicate")
	public static Response duplicate(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody BasicPackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		long newId = rand.nextLong();
		
		if (!AccountDB.duplicatePack(req.id, session.userId, newId)) throw new ApiException("Unknown Pack", HttpStatus.NOT_FOUND);
		PackDB.duplicatePack(req.id, session.userId, newId);
		
		return new Response(true);
	}
	
	@PostMapping("/delete")
	public static Response delete(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody BasicPackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		if (!AccountDB.deletePack(req.id, session.userId)) throw new ApiException("Unknown Pack", HttpStatus.NOT_FOUND);
		PackDB.deletePack(req.id, session.userId);
		
		return new Response(true);
	}
	
	
	@PostMapping("/data/{id}/upload")
	public static Response upload(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody Texture req, @PathVariable(value="id") long pack, @RequestParam @Pattern(regexp="^[a-b\\/]{1,100}$") String path) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		System.out.println("POST path: "+path+" pack: "+pack+" userId: "+session.userId);
		
		String version = AccountDB.getPackVersion(pack, session.userId);
		if (version == null) throw new ApiException("Unknown Pack");
		if (!VersionControl.isItem(version, path)) throw new ApiException("Unknown item");
		
		PackDB.insertItem(pack, session.userId, path, req);
		
		return new Response(true);
	}
	
	@GetMapping("/data/{id}/get")
	public static Response get(@CookieValue(value = "session_token", defaultValue = "") String token, @PathVariable(value="id") long pack, @RequestParam @Pattern(regexp="^[a-b\\/]{1,100}$") String path) throws SQLException, StreamReadException, DatabindException, IOException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		System.out.println("GET path: "+path+" pack: "+pack+" userId: "+session.userId);
		
		Document item = PackDB.getItem(pack, session.userId, path);
		if (item == null) {
			String version = AccountDB.getPackVersion(pack, session.userId);
			if (version == null) throw new ApiException("Unknown Pack");
			
			File file = new File(System.getProperty("user.dir")+("/assets/"+version+"/"+path+".json").replace('/', File.separatorChar));
			System.out.println(file.getPath());
			if (!file.exists()) throw new ApiException("Unknown Item");
			
			Document t = mapper.readValue(file, Document.class);
			if (t == null) throw new ApiException("Unknown Item");
			
			return new TextureResponse(t);
		}
		
		return new TextureResponse(item);
	}
	
	@GetMapping("/data/{id}/full")
	public static Response get(@CookieValue(value = "session_token", defaultValue = "") String token, @PathVariable(value="id") long id) throws IOException, SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		String version = AccountDB.getPackVersion(id, session.userId);
		if (version == null) throw new ApiException("Unknown Pack");
		int format = VersionControl.getFormat(version);
		
		PackData pack = PackDB.getFullPackData(id, session.userId);
		if (pack == null) throw new ApiException("Unknown Pack");
		
		return new FullPackResponse(version, format, pack.data);
	}
	
	@SuppressWarnings("unused")
	@PostMapping("/upload")
	public static Response get(@CookieValue(value = "session_token", defaultValue = "") String token, @Validated @RequestBody ImportPackRequest req) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session == null) throw new ApiException("Invalid Session");
		
		String version = VersionControl.getVersion(req.format);
		if (version == null) throw new ApiException("Invalid Version");
		
		if (req.data.isEmpty()) throw new ApiException("You cannot import an empty pack!");
		
		Iterator<Entry<String, Texture>> it = req.data.entrySet().iterator();
		while (it.hasNext()) {
			if (!VersionControl.isItem(version, it.next().getKey())) it.remove();
		}
		
		if (req.data.isEmpty()) throw new ApiException("Pack contains invalid items");
		
		long id = rand.nextLong();
		AccountDB.addPack(id, session.userId, req.name, version);
		PackDB.createPack(id, session.userId);
		PackDB.insertItems(id, session.userId, req.data);
		
		return new Response(true);
	}
	
}