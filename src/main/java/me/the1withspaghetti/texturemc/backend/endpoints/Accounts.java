package me.the1withspaghetti.texturemc.backend.endpoints;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", methods= {RequestMethod.POST, RequestMethod.GET})
public class Accounts {
	
	
}