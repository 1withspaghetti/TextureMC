package me.the1withspaghetti.texturemc.frontend;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.the1withspaghetti.texturemc.backend.database.AccountDB;
import me.the1withspaghetti.texturemc.backend.service.SessionService;

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
				return "redirect: /accounts/";
			} else {
				model.addAttribute("error", "Email confirmation expired, please re-send a confirmation email.");
				return "error";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("error", "Internal Server Error, please try again.");
		return "error";
	}
	
	/*@GetMapping("/account")
	public ModelAndView getAccount(@CookieValue(value = "session_token", defaultValue = "") String token) {
		Map<String, Object> model = new HashMap<String, Object>();
		UUID userId = Accounts.checkToken(token);
		if (userId != null) 
			model.put("hasSession", true);
		else 
			return new ModelAndView("redirect:/login");
		
		try {
			UserData data = AccountManager.getUserData(userId);
			List<HtmlTexturePack> packList = new ArrayList<HtmlTexturePack>();
			if (!data.packs.isEmpty()) {
				for (String packStr: data.packs.split(":")) {
					TexturePackMin pack = PiskelManager.getPackMin(UUID.fromString(packStr), userId);
					packList.add(new HtmlTexturePack(pack.name, pack.version, pack._id.toUUID().toString()));
				}
			}
			model.put("username", data.username);
			model.put("texturePacks", packList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("account", model);
	}
	
	@GetMapping("/editor")
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
