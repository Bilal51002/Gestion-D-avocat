package org.baeldung.security;

import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.baeldung.service.SecretaireService;
import org.baeldung.service.pfe.IUserService;

import java.util.ArrayList;
import java.util.List;

public class ActiveUserStore {

        private List<String> users;

        private IUserService userService;  // Service pour récupérer des informations sur User
        private SecretaireService secretaireService;  // Service pour récupérer des informations sur Secretaire

        // Constructeur
        public ActiveUserStore(IUserService userService, SecretaireService secretaireService) {
            this.users = new ArrayList<String>();
            this.userService = userService;
            this.secretaireService = secretaireService;
        }

        // Getter et Setter pour la liste des utilisateurs actifs
        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        // Méthode pour vérifier si l'email appartient à un User ou Secretaire actif
        public boolean isUserOrSecretaireActive(String email) {
            // Vérifier si l'email appartient à un User existant
            User user = userService.getUser(email);
            if (user != null) {
                return true;  // Si un User est trouvé, l'utilisateur est actif
            }

            // Vérifier si l'email appartient à un Secretaire existant
            Secretaire secretaire = secretaireService.findByEmail(email);
            return secretaire != null;  // Si un Secretaire est trouvé, l'utilisateur est actif
        }

        // Méthode pour ajouter un utilisateur actif (User ou Secretaire) à la liste
        public void addUser(String email) {
            if (isUserOrSecretaireActive(email)) {
                users.add(email);
            } else {
                System.out.println("L'email n'est pas associé à un User ou Secretaire valide.");
            }
        }

        // Méthode pour supprimer un utilisateur actif de la liste
        public void removeUser(String email) {
            users.remove(email);
        }
}
