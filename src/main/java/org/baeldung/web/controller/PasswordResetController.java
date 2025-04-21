package org.baeldung.web.controller;

import org.baeldung.persistence.model.PasswordResetToken;
import org.baeldung.persistence.model.User;
import org.baeldung.service.PasswordResetTokenService;
import org.baeldung.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private UserService userService;

    // Afficher la page pour demander un reset de mot de passe
    @GetMapping("/Admin/AdminAvocat/Avocat/resetPassword")
    public String showResetPasswordPage() {
        return "/Admin/AdminAvocat/Avocat/resetPassword"; // Vue pour demander un email
    }

   // th:href="@{/Admin/AdminAvocat/Avocat/resetPassword}"
    // Soumettre la demande de réinitialisation
    @PostMapping("/Admin/AdminAvocat/Avocat/resetPassword")
    public String processResetPasswordRequest(@RequestParam("email") String email, Model model) {
        // Cette méthode retourne un User et non un Optional<User>
        User user = userService.findUserByEmail(email);

        if (user != null) {
            // Générer le token de réinitialisation
            PasswordResetToken resetToken = passwordResetTokenService.createPasswordResetTokenForUser(user);

            // Envoi d'un email avec le lien de réinitialisation
            model.addAttribute("message", "Un email de réinitialisation a été envoyé.");
        } else {
            model.addAttribute("error", "Aucun utilisateur trouvé avec cet email.");
        }

        return "/Admin/AdminAvocat/Avocat/resetPassword"; // Retour à la page de demande
    }


    // Afficher la page pour saisir le nouveau mot de passe
    @GetMapping("/Admin/AdminAvocat/Avocat/resetPassword/{token}")
    public String showChangePasswordPage(@PathVariable("token") String token, Model model) {
        // Retourner directement l'objet PasswordResetToken, qui peut être null si non trouvé
        PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);

        if (passwordResetToken != null) {
            model.addAttribute("token", token);
            return "/Admin/AdminAvocat/Avocat/changePassword"; // Vue pour saisir le nouveau mot de passe
        } else {
            model.addAttribute("error", "Token invalide ou expiré.");
            return "/Admin/AdminAvocat/Avocat/resetPassword"; // Retourner à la page de réinitialisation
        }
    }


    // Soumettre le nouveau mot de passe
    @PostMapping("/Admin/AdminAvocat/Avocat/resetPassword/{token}")
    public String processChangePassword(@PathVariable("token") String token,
                                        @RequestParam("password") String newPassword, Model model) {
        // Retourner directement l'objet PasswordResetToken (qui peut être null)
        PasswordResetToken resetToken = passwordResetTokenService.getPasswordResetToken(token);

        if (resetToken != null) {
            User user = resetToken.getUser();  // Récupérer l'utilisateur associé au token
            passwordResetTokenService.updatePassword(user, newPassword);  // Mettre à jour le mot de passe
            model.addAttribute("message", "Votre mot de passe a été réinitialisé avec succès.");
            return "login";  // Ou rediriger vers la page de connexion
        } else {
            model.addAttribute("error", "Token invalide ou expiré.");
            return "/Admin/AdminAvocat/Avocat/resetPassword";  // Retourner à la page de réinitialisation
        }
    }

}

