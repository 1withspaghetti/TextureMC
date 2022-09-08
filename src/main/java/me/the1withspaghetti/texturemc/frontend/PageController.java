package me.the1withspaghetti.texturemc.frontend;

import org.springframework.stereotype.Controller;

@Controller
public class PageController {
	
	/*@GetMapping("/")
	public ModelAndView getHome(@CookieValue(value = "session_token", defaultValue = "") String token) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (Accounts.checkToken(token) != null) 
			model.put("hasSession", true);
		return new ModelAndView("home", model);
	}
	
	@GetMapping("/about")
	public ModelAndView getAbout(@CookieValue(value = "session_token", defaultValue = "") String token) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (Accounts.checkToken(token) != null) 
			model.put("hasSession", true);
		return new ModelAndView("about", model);
	}
	
	@GetMapping("/login")
	public ModelAndView getLogin(@CookieValue(value = "session_token", defaultValue = "") String token) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (Accounts.checkToken(token) != null) 
			model.put("hasSession", true);
		return new ModelAndView("login", model);
	}
	
	@GetMapping("/signup")
	public ModelAndView getSignup(@CookieValue(value = "session_token", defaultValue = "") String token) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (Accounts.checkToken(token) != null) 
			model.put("hasSession", true);
		return new ModelAndView("signup", model);
	}
	
	@GetMapping("/account")
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
	}
	
	@GetMapping("/error")
	public ModelAndView getError(@CookieValue(value = "session_token", defaultValue = "") String token, Model model) {
		if (Accounts.checkToken(token) != null) 
			model.addAttribute("hasSession", true);
		return new ModelAndView("error", model.asMap());
	}*/
}
