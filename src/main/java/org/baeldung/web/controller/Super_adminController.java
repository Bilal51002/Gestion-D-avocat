package org.baeldung.web.controller;

import java.util.Base64;
import java.util.List;
import java.util.Base64.Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.Ville;
import org.baeldung.service.AvocatService;
import org.baeldung.service.BureauAvocatService;
import org.baeldung.service.ClientService;
import org.baeldung.service.DossierService;
import org.baeldung.service.SecretaireService;
import org.baeldung.service.TypeDossierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
@Component
public class Super_adminController {

	@Autowired
	BureauAvocatService bureauAvocatService;
	@Autowired
	AvocatService avocatService;
	@Autowired
	DossierService dossierService;
	@Autowired
	ClientService clientService;
	@Autowired
	TypeDossierService typeDossierService;
	@Autowired
	SecretaireService secretaireService;

	@RequestMapping(value = "/super_admin/index")
	public ModelAndView wxAutoLogin1(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {

		System.err.println("index");
		/*
		 * List<Secretaire> secretaire =secretaireService.findByfirstName(mt);
		 * List<Barreau> barreaux2 =secretaireService.findBynomBarreau(mv);
		 * List<Barreau> barreaux3 =secretaireService.findAllBarreaux(); List<Ville>
		 * villess =secretaireService.findAllVilles();
		 * model.addAttribute("barreaux2",barreaux2);
		 * model.addAttribute("barreaux3",barreaux3); model.addAttribute("villess",
		 * villess);
		 */

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		/*
		 * System.out.println(encoder.encodeToStringavocats.get(0).getImguser().getBytes
		 * ()));
		 */
		/* model.addAttribute("secretaires",secretaires); */
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn" + user.getEmail());
		if (user == null) {
			ret.addObject("user", user);
		}
		return ret;

	}

}
