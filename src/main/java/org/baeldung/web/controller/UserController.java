package org.baeldung.web.controller;

import java.util.Locale;

import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Client;
import org.baeldung.security.ActiveUserStore;
import org.baeldung.service.ClientService;
import org.baeldung.service.pfe.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @Autowired
    ClientService clientService;

    @RequestMapping(value = "/loggedUsers", method = RequestMethod.GET)
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }

    @RequestMapping(value = "/loggedUsersFromSessionRegistry", method = RequestMethod.GET)
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userService.getUsersFromSessionRegistry());
        return "users";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Retourne la vue de connexion
    }

    @PostMapping("/loginSuccess")
    public String handleLoginSuccess(Authentication authentication) {
        // Vérification de l'objet principal d'authentification
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            User user = (User) userDetails;
            String role = user.getRoles().iterator().next().getName();  // Prend le premier rôle de l'utilisateur

            // Redirige selon le rôle de l'utilisateur
            switch (role) {
                case "ROLE_ADMIN_APPLICATION":
                    return "redirect:/admin/dashboard";
                case "ROLE_ADMIN_AVOCAT":
                    return "redirect:/admin_avocat/dashboard";
                case "ROLE_AVOCAT":
                    return "redirect:/avocat/dashboard";
                case "ROLE_SECRETAIRE":
                    return "redirect:/Secretaire/secretaires";
                case "ROLE_CLIENT":
                    return "redirect:/Client/index";
                default:
                    return "redirect:/login?error";
            }
        }
        return "redirect:/login?error";
    }
//    @GetMapping("/Client/index")
//    public String clientDashboard(Model model, Authentication authentication) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        System.out.println("User email: " + userDetails.getUsername());  // Ajoutez un log pour vérifier l'email
//        Client client = clientService.findByEmail(userDetails.getUsername());
//
//
////        if (client == null) {
////            // Si le client n'est pas trouvé, afficher une page d'erreur ou rediriger
////            model.addAttribute("error", "Client not found");
////            return "errorPage";  // Remplacez "errorPage" par votre page d'erreur
////        }
//
//        // Ajoutez les informations du client, des dossiers, des rendez-vous et des frais
//        model.addAttribute("client", client);
//        model.addAttribute("dossiers", client.getDossier());
//        //model.addAttribute("rendezvous", client.getRdv());
//        //model.addAttribute("frais", client.getFrais());
//
//        return "Client/index"; // Redirige vers la page d'accueil du client
//    }
}
