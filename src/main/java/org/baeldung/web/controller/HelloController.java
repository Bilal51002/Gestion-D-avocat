
package org.baeldung.web.controller;

import java.util.List;

import org.baeldung.persistence.model.User;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.Ville;
import org.baeldung.service.AvocatService;
import org.baeldung.service.BureauAvocatService;
import org.baeldung.service.ClientService;
import org.baeldung.service.DossierService;
import org.baeldung.service.SecretaireService;
import org.baeldung.service.TypeDossierService;
import org.baeldung.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.Base64.Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HelloController {
	@Autowired
	BureauAvocatService bureauAvocatService;

	  @Autowired
	  DossierService dossierService;
	  @Autowired
	  AvocatService avocatService;
	  @Autowired
	  UserService userService;

	  @Autowired
		ClientService clientService;
		@Autowired
		TypeDossierService typeDossierService;
		@Autowired
		SecretaireService secretaireService;
	/* BureauAvocatService bureauAvocatService2 = new BureauAvocatService(); */
	@Autowired
	private UserRepository repo;

	 @DeleteMapping("/Admin/Avocat/delete/")
	  public void deleteDossier(@PathVariable Long id) {
	  dossierService.deleteDossier(id);
		/* return new ResponseEntity<>(); */
	 }


	@RequestMapping(value="/")
	public String home(Model model, @RequestParam(name = "query", defaultValue = "") String mc, @RequestParam(name = "query", defaultValue = "") String mu) throws Exception {
		System.err.println("Home");

				/* HttpSession session = request.getSession(); */
				/* List<BureauAvocat> listbureau = bureauAvocatService.listAll(); */
				List<org.baeldung.persistence.model.User> users=userService.findByfirstName(mu);
				List<BureauAvocat> bureaux = bureauAvocatService.findByNom(mc);
				List<Barreau> barreaux = bureauAvocatService.findAllBarreaux();
				List<Ville> villes = bureauAvocatService.findAllVilles();
				/* List<Avocat> avocats = bureauAvocatService.findByName(); */

				Encoder encoder = Base64.getEncoder();
				//System.out.println(bureaux.size());
				//System.out.println(encoder.encodeToString(bureaux.get(0).getImage().getBytes()));

				/* model.addAttribute("bureaux", new BureauAvocat(null, mc, mc, mc, mc, mc,0,mc, null));*/
				model.addAttribute("users", users);
				model.addAttribute("bureaux", bureaux);
				model.addAttribute("barreaux", barreaux);
				model.addAttribute("villes", villes);

	return "home";}

	@RequestMapping("Admin/Avocat/employee-dashboard")
	public String employeedashboard() {
		return "employee-dashboard";
	}

	@RequestMapping("Admin/activities")
	public String activities() {
		return "activities";
	}

	@GetMapping("/recherche")
	public String home1(Model model , @RequestParam(name = "query", defaultValue = "") String mc) {
		System.err.println("Home");

		/* HttpSession session = request.getSession(); */
		/* List<BureauAvocat> listbureau = bureauAvocatService.listAll(); */

		List<BureauAvocat> bureaux = bureauAvocatService.findByNom(mc);
		List<Barreau> barreaux = bureauAvocatService.findAllBarreaux();
		List<Ville> villes = bureauAvocatService.findAllVilles();
		/* List<Avocat> avocats = bureauAvocatService.findByName(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(bureaux.size());
	//	System.out.println(encoder.encodeToString(bureaux.get(0).getImage().getBytes()));

		/* model.addAttribute("bureaux", new BureauAvocat(null, mc, mc, mc, mc, mc,0,mc, null));*/

		model.addAttribute("bureaux", bureaux);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("villes", villes);
		/* model.addAttribute("avocats", avocats); */
		return "home";
	}


	@GetMapping("/Client/client-profile")
	public String clientprofile(Model model, @RequestParam("bureau") Long bureau) {
		BureauAvocat result = bureauAvocatService.findById(bureau);
		System.err.println(result.getNom());
		model.addAttribute("bureau", result);
		return "client-profile";
	}
	@GetMapping("/client-profile")
	public String clientprofil(Model model, @RequestParam("bureau") Long bureau) {
		BureauAvocat result = bureauAvocatService.findById(bureau);
		System.err.println(result.getNom());
		model.addAttribute("bureau", result);
		return "client-profile";
	}


	@RequestMapping("/registration")
	public String registration(Model model, @RequestParam(name = "query", defaultValue = "") String mc) {
		System.err.println("register");
		List<Ville> villes = bureauAvocatService.findAllVilles();
		List<Barreau> barreaux = bureauAvocatService.findAllBarreaux();
		List<BureauAvocat> bureaux = bureauAvocatService.findByNom(mc);
		model.addAttribute("bureaux", bureaux);
		model.addAttribute("villes", villes);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("user", new User());

		return "registration";
	}


	 @PostMapping("/process_register") public String processRegistration(User user) {
		 repo.save(user);
		 System.out.println("mahmoud");

	 return "Client/index"; }

	@RequestMapping("/Admin/Avocat/clients")
	public String clients() {
		return "clients";
	}

	@RequestMapping("/Client/AddFolder")
	public String AddFolder(HttpServletRequest request, Model model) {


		HttpSession session = request.getSession();
		User user =(User)session.getAttribute("user1");
		if(user!=null) {
			model.addAttribute("user", user);
		}
		return "Client/AddFolder";
	}


	@RequestMapping("/Client/ajouterDossier")
	public String ajouterDossier() {
		return "ajouterDossier";
	}

	@RequestMapping("/Admin/Avocat/clients-list")
	public String clientslist() {
		return "clients-list";
	}



	@RequestMapping("/Admin/chat")
	public String chat() {
		return "chat";
	}

	@RequestMapping("/register")
	public String resgister() {
		return "register";
	}

	@RequestMapping("/forgetPassword")
	public String forgetPassword() {
		return "forgetPassword";
	}

	@RequestMapping("/registra")
	public String registra() {
		return "registra";
	}

}
