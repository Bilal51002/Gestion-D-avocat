package org.baeldung.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class registetypecontroller{
	@GetMapping("/register")
	public String getregistertype() {
	return "register";
	}
}
