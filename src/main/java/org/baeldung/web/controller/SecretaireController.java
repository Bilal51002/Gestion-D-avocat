package org.baeldung.web.controller;

import org.baeldung.persistence.model.pfe.RDV;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.baeldung.service.AvocatService;
import org.baeldung.service.RdvService;
import org.baeldung.service.SecretaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/Secretaire")
public class SecretaireController {

    private static final Logger logger = LoggerFactory.getLogger(SecretaireController.class);

    @Autowired
    private SecretaireService secretaireService;

    @Autowired
    private RdvService rdvService;

    @Autowired
    private AvocatService avocatService;

    @GetMapping("/Dashboard")
    public String dashboard(Model model, @RequestParam(name = "lang", required = false) String lang) {

        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        logger.info("Tentative de récupération de la secrétaire avec l'email: {}", email);

        Secretaire secretaire = secretaireService.findByEmail(email);

        if (secretaire == null) {
            logger.error("Impossible de trouver la secrétaire avec l'email: {}", email);
            // Créer un objet Secretaire temporaire pour éviter l'erreur NullPointerException
            secretaire = new Secretaire();
            secretaire.setFirstName("");
            secretaire.setLastName("");
        } else {
            logger.info("Secrétaire trouvée: {} {}", secretaire.getFirstName(), secretaire.getLastName());
        }

        // Ajouter les informations nécessaires au modèle
        model.addAttribute("secretaire", secretaire);

        // Récupérer les RDV pour affichage
        List<RDV> rdvs = rdvService.findAll();
        model.addAttribute("rdvs", rdvs);

        // Ajouter les avocats pour permettre d'associer un RDV à un avocat
        model.addAttribute("avocats", avocatService.findAll());

        return "Secretaire/Dashboard";
    }
}