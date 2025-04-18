package org.baeldung.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AdminIndex {


	 @GetMapping("/Admin/AdminAvocat/Client/chat") public String getchatIndex() {

		 return"/Admin/AdminAvocat/Client/chat"; }
	 @GetMapping("/Admin/AdminAvocat/Secretaire/chat") public String getsecretaireIndex() {

		 return"/Admin/AdminAvocat/Secretaire/chat"; }
	 @GetMapping("/index") public String getAdminIndex() {

		 return"index"; }

     @GetMapping("/Admin/AdminAvocat/Avocat/chat") public String getAdminIndex1() {

	return"/Admin/AdminAvocat/Avocat/chat"; }



	@GetMapping("/clients")
    public String getAdminProfil() {
        return "Admin/clients";
    }}

