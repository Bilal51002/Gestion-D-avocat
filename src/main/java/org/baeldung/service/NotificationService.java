package org.baeldung.service;

import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.pfe.ClientRepository;
import org.baeldung.persistence.dao.pfe.NotificationRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Client;
import org.baeldung.persistence.model.pfe.Notification;
import org.baeldung.persistence.model.pfe.RDV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;

    public List<Notification> getRecentNotifications(int limit) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Récupérer toutes les notifications de l'utilisateur
        List<Notification> allNotifications = getNotificationsByUser(currentUser);

        // Trier par date de création (plus récentes en premier)
        allNotifications.sort((n1, n2) -> n2.getDateCreation().compareTo(n1.getDateCreation()));

        // Limiter le nombre de résultats
        if (allNotifications.size() > limit) {
            return allNotifications.subList(0, limit);
        }

        return allNotifications;
    }


    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByDestinataireOrderByDateCreationDesc(user);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public void marquerCommeLue(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }

    public void creerNotificationDemandeRDV(RDV rdv) {
        // Trouver tous les utilisateurs ayant le rôle SECRETAIRE
        List<User> secretaires = userRepository.findByRoles_Name("ROLE_SECRETAIRE");

        for (User secretaire : secretaires) {
            Notification notification = new Notification();
            notification.setTitre("طلب موعد جديد");
            notification.setMessage("العميل " + rdv.getClient().getLastName() + " " + rdv.getClient().getFirstName() +
                    " طلب موعدًا يوم " + rdv.getDate() + " في الساعة " + rdv.getHeur());
            notification.setRendezVous(rdv);
            notification.setDestinataire(secretaire);
            notification.setType("RDV_DEMANDE");

            save(notification);
        }
    }
    public void creerNotificationRDVAccepte(RDV rdv) {
        // Trouver l'utilisateur associé au client
        Client client = clientRepository.findById(rdv.getId()).orElse(null);
        if (client == null) {
            return; // Ne pas créer de notification si l'utilisateur n'existe pas
        }

        Notification notification = new Notification();
        notification.setTitre("Rendez-vous accepté");
        notification.setMessage("Votre rendez-vous du " + rdv.getDate() + " à " + rdv.getHeur() +
                " avec Maître " + rdv.getAvocat().getLastName() + " a été accepté.");
        notification.setRendezVous(rdv);
        notification.setDestinataire(client);
        notification.setType("RDV_ACCEPTE");

        save(notification);
    }

    public void creerNotificationRDVRefuse(RDV rdv) {
        // Trouver l'utilisateur associé au client
        Client client = clientRepository.findById(rdv.getId()).orElse(null);
        if (client == null) {
            return; // Ne pas créer de notification si l'utilisateur n'existe pas
        }

        Notification notification = new Notification();
        notification.setTitre("Rendez-vous refusé");
        notification.setMessage("Votre rendez-vous du " + rdv.getDate() + " à " + rdv.getHeur() +
                " avec Maître " + rdv.getAvocat().getLastName() + " a été refusé.");
        notification.setRendezVous(rdv);
        notification.setDestinataire(client);
        notification.setType("RDV_REFUSE");

        save(notification);
    }

    // Cette méthode doit être implémentée dans le NotificationRepository
    public long countNonLues(User user) {
        return notificationRepository.countByDestinataireAndLu(user, false);
    }
}