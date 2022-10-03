package me.the1withspaghetti.texturemc.frontend;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import me.the1withspaghetti.texturemc.backend.database.AccountDB;
import me.the1withspaghetti.texturemc.backend.database.AccountDB.Pack;
import me.the1withspaghetti.texturemc.backend.database.AccountDB.User;
import me.the1withspaghetti.texturemc.backend.endpoints.Packs;
import me.the1withspaghetti.texturemc.backend.service.SessionService;
import me.the1withspaghetti.texturemc.backend.service.SessionService.SessionData;

@Controller
public class PageController {
	
	@GetMapping({"/","/index.html"})
	public String getHome(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "";
	}
	
	@GetMapping({"/about","/about/index.html"})
	public String getAbout(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "about";
	}
	
	@GetMapping({"/login","/login/index.html"})
	public String getLogin(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "login";
	}
	
	@GetMapping({"/signup","/signup/index.html"})
	public String getSignup(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "signup";
	}
	
	@GetMapping({"/confirm-email","/signup/index.html"})
	public String getEmailConfirm(Model model, @CookieValue(value = "session_token", defaultValue = "") String token, @RequestParam(name="confirmation") String idStr, HttpServletResponse res) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		
		try {
			long id = Long.parseLong(idStr);
			long user = AccountDB.getEmailConfirmation(id);
			if (user != 0) {
				AccountDB.confirmUser(id, user);
				UUID session = SessionService.newSession(user, true);
				Cookie newToken = new Cookie("session_token", session.toString());
				newToken.setSecure(false);
				newToken.setPath("/");
				newToken.setMaxAge((int) TimeUnit.HOURS.toSeconds(1));
				res.addCookie(newToken);
				return "redirect:/account/";
			} else {
				model.addAttribute("error", "Email confirmation expired, please re-send a confirmation email.");
				return "error";
			}
		} catch (NumberFormatException e) {
			model.addAttribute("error", "Invalid confirmation Id.");
			return "error";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("error", "Internal Server Error, please try again.");
		return "error";
	}
	
	@GetMapping({"/account","/account/index.html"})
	public String getAccount(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		SessionData session = SessionService.getSession(token);
		if (session != null) 
			model.addAttribute("hasSession", true);
		else {
			session = SessionService.getUnverifiedSession(token);
			if (session == null) return "redirect:/login/";
			model.addAttribute("error", "You need to verify your account!");
			model.addAttribute("error_extra", "Check your email (and spam folder) for a message.");
			return "error";
		}
		
		try {
			User user = AccountDB.getUser(session.userId);
			
			LinkedList<Pack> packs = AccountDB.getPacks(session.userId);
			
			model.addAttribute("username", user.username);
			model.addAttribute("packs", packs);
			model.addAttribute("isMax", packs.size() >= Packs.MAX_PACKS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "account";
	}
	
	@GetMapping("/editor/index.html")
	public String wrongEditor() {
		return "redirect:/account/";
	}
	
	@GetMapping("/editor/{id}")
	public String getEditor(Model model, @CookieValue(value = "session_token", defaultValue = "") String token, @PathVariable(value="id") long id) throws SQLException {
		SessionData session = SessionService.getSession(token);
		if (session != null) 
			model.addAttribute("hasSession", true);
		else 
			return "redirect:/login/";
		
		Pack pack = AccountDB.getPack(id, session.userId);
		if (pack == null) return "redirect:/account/?error_msg=Unknown%20Pack";
		
		model.addAttribute("pack_id", String.valueOf(pack.id));
		model.addAttribute("pack_name", pack.name);
		model.addAttribute("pack_version", pack.version);
		
		return "editor";
	}
	
	@GetMapping({"/error","/error/index.html"})
	public String getError(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "error";
	}
}
