package org.baeldung.web.controller;

import java.util.Locale;
import org.baeldung.security.ActiveUserStore;
import org.baeldung.service.ClientService;
import org.baeldung.service.pfe.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//@RestController
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

        /**
         * Endpoint pour activer un utilisateur.
         * @param id L'ID de l'utilisateur à activer.
         * @return Une réponse HTTP indiquant si l'activation a réussi ou non.
         */
        @PutMapping("/users/activate/{id}")
        public ResponseEntity<String> activateUser(@PathVariable Long id) {
            boolean isActivated = userService.activateUser(id);

            if (isActivated) {
                return ResponseEntity.ok("Utilisateur activé avec succès.");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

}
