package org.baeldung.web.controller;

import java.util.Base64;
import java.util.List;
import java.util.Base64.Encoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.Ville;
import org.baeldung.service.AvocatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Component
@Controller
public class Super_adminController {

	@Autowired
	private AvocatService avocatService;

	@GetMapping("/super_admin/index")
	public ModelAndView handleSuperAdminIndex(
			HttpServletRequest request,
			Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) {

		ModelAndView modelAndView = new ModelAndView("index"); // Nom de la vue à retourner

		// Charger les données nécessaires
		List<Avocat> avocats = avocatService.findByfirstName(mc);
		List<Barreau> barreauxByNom = avocatService.findBynomBarreau(mv);
		List<Barreau> tousBarreaux = avocatService.findAllBarreaux();
		List<Ville> toutesVilles = avocatService.findAllVilles();
		List<Avocat> allAvocats = avocatService.findAll();

		// Ajouter les données dans le modèle
		model.addAttribute("avocats", allAvocats);
		model.addAttribute("barreaux", tousBarreaux);
		model.addAttribute("barreauxByNom", barreauxByNom);
		model.addAttribute("villes", toutesVilles);

		// Encodage des données utilisateur (si nécessaire)
		Encoder encoder = Base64.getEncoder();
		if (!allAvocats.isEmpty() && allAvocats.get(0).getImguser() != null) {
			System.out.println("Exemple encodé : " + encoder.encodeToString(allAvocats.get(0).getImguser().getBytes()));
		}

		// Vérification de l’utilisateur en session
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		if (user != null) {
			System.out.println("Utilisateur connecté : " + user.getEmail());
			modelAndView.addObject("user", user);
		} else {
			System.out.println("Aucun utilisateur trouvé dans la session.");
		}

		return modelAndView;
	}
}