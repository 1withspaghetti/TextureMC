package me.the1withspaghetti.texturemc.frontend;

import java.sql.SQLException;
import java.util.LinkedList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import me.the1withspaghetti.texturemc.backend.database.AccountDB;
import me.the1withspaghetti.texturemc.backend.database.AccountDB.Pack;
import me.the1withspaghetti.texturemc.backend.database.AccountDB.User;
import me.the1withspaghetti.texturemc.backend.service.SessionService;
import me.the1withspaghetti.texturemc.backend.service.SessionService.SessionData;

@Controller
public class PageController {
	
	@GetMapping({"/","/index.html"})
	public String getHome(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "";
	}
	
	@GetMapping({"/about/","/about/index.html"})
	public String getAbout(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "about";
	}
	
	@GetMapping({"/login/","/login/index.html"})
	public String getLogin(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "login";
	}
	
	@GetMapping({"/signup/","/signup/index.html"})
	public String getSignup(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "signup";
	}
	
	@GetMapping({"/confirm-email"})
	public String getEmailConfirm(Model model, @CookieValue(value = "session_token", defaultValue = "") String token, @RequestParam(name="confirmation") String idStr) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		
		try {
			long id = Long.parseLong(idStr);
			long user = AccountDB.getEmailConfirmation(id);
			if (user != 0) {
				AccountDB.confirmUser(id, user);
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
	
	@GetMapping("/account")
	public String getAccount(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		SessionData session = SessionService.getSession(token);
		if (session != null) 
			model.addAttribute("hasSession", true);
		else 
			return "redirect:/login/";
		
		try {
			User user = AccountDB.getUser(session.userId);
			
			if (!user.verified) {
				// TODO
			}
			
			LinkedList<Pack> packs = AccountDB.getPacks(session.userId);
			
			model.addAttribute("username", user.username);
			model.addAttribute("packs", packs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "account";
	}
	
	/*@GetMapping("/editor")
	public ModelAndView getEditor(@CookieValue(value = "session_token", defaultValue = "") String token, @RequestParam("pack") String packId) {
		Map<String, Object> model = new HashMap<String, Object>();
		UUID userId = Accounts.checkToken(token);
		if (userId != null && packId != null) 
			model.put("hasSession", true);
		else 
			return new ModelAndView("redirect:/login");
		
		try {
			TexturePackMin pack = PiskelManager.getPackMin(UUID.fromString(packId), userId);
			model.put("packName", pack.name);
			model.put("packVersion", pack.version);
		} catch (ApiException e) {
			return new ModelAndView("redirect:/account");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("editor", model);
	}*/
	
	@GetMapping("/error")
	public String getError(Model model, @CookieValue(value = "session_token", defaultValue = "") String token) {
		model.addAttribute("hasSession", SessionService.getSession(token) != null);
		return "error";
	}
}
