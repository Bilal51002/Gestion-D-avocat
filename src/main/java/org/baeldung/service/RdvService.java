package org.baeldung.service;

import org.baeldung.persistence.dao.pfe.RdvRepository;
import org.baeldung.persistence.model.pfe.*;
import org.baeldung.service.pfe.RdvServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class RdvService implements RdvServiceInterface {

    @Autowired
    private RdvRepository rdvRepository;

    @Autowired
    private AvocatService avocatService;
    @Autowired
    private ClientService clientService;

    @Autowired
    private BureauAvocatService bureauAvocatService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MailClient mailClient;

    /**
     * Crée un nouveau rendez-vous en tenant compte du type de visite (première ou non)
     */
    public RDV creerRendezVous(RDV rdv) {
        // Vérifier si le bureau est spécifié (obligatoire dans tous les cas)
        if (rdv.getBureau() == null) {
            throw new IllegalStateException("Un bureau doit être sélectionné pour tous les rendez-vous");
        }

        // Vérifier la date et l'heure
        if (rdv.getDate() == null || rdv.getHeur() == null) {
            throw new IllegalStateException("La date et l'heure du rendez-vous doivent être spécifiées");
        }

        // Vérifier la disponibilité selon le type de visite
        Boolean estPremiereFois = rdv.isPremiereFois();

        if (estPremiereFois != null && estPremiereFois) {
            // Cas 1: Première visite (لا، هذه أول مرة) - Vérifier la disponibilité du bureau
            List<String> heuresDisponibles = getHeuresDisponiblesBureau(rdv.getBureau().getId(), rdv.getDate());
            if (!heuresDisponibles.contains(rdv.getHeur())) {
                throw new IllegalStateException("Ce créneau n'est plus disponible pour ce bureau");
            }

            // S'assurer qu'aucun avocat n'est associé pour une première visite
            rdv.setAvocat(null);
        } else {
            // Cas 2: Visite régulière (نعم، لقد سبق وأن حجزت موعداً) - Vérifier la disponibilité de l'avocat
            if (rdv.getAvocat() == null) {
                throw new IllegalStateException("Un avocat doit être sélectionné pour les rendez-vous réguliers");
            }

            List<String> heuresDisponibles = getHeuresDisponibles(rdv.getAvocat().getId(), rdv.getDate());
            if (!heuresDisponibles.contains(rdv.getHeur())) {
                throw new IllegalStateException("Ce créneau n'est plus disponible pour cet avocat");
            }

            // Vérifier que l'avocat appartient bien au bureau sélectionné
            if (!avocatService.verifierAppartenanceBureau(rdv.getAvocat().getId(), rdv.getBureau().getId())) {
                throw new IllegalStateException("L'avocat sélectionné n'appartient pas au bureau spécifié");
            }
        }

        // Définir le statut par défaut
        rdv.setStatut(Statut.EN_ATTENTE);

        // Sauvegarder le rendez-vous
        RDV savedRdv = rdvRepository.save(rdv);

        // Créer notification pour les secrétaires
        notificationService.creerNotificationDemandeRDV(savedRdv);

        return savedRdv;
    }
    public Page<RDV> findByClientId(Long clientId, Pageable pageable) {
        return rdvRepository.findByClientId(clientId, pageable);
    }

    public Page<RDV> findAllPaginated(Pageable pageable) {
        return rdvRepository.findAll(pageable);
    }
    public long countTodayAppointments() {
        LocalDate today = LocalDate.now();

        // Si vous utilisez la première méthode du repository
        return rdvRepository.countByDateEquals(today);
    }


    public RDV findNextByClient(Long clientId) {
        return rdvRepository.findFirstByClientIdAndDateAfterOrderByDateAsc(clientId, LocalDateTime.now().toLocalDate());
    }
    // Dans RdvService.java
    public List<RDV> findAllByClient(Long clientId) {
        // Cette implémentation dépend de votre structure de données et de votre repository
        return rdvRepository.findByClientIdOrderByDateDesc(clientId);
    }
    public boolean estPremiereFois(Long clientId) {
        // Compter le nombre de rendez-vous du client
        long nombreRendezVous = rdvRepository.countByClientId(clientId);
        return nombreRendezVous == 0;
    }



    @Transactional
    public void accepterRendezVous(Long id) {
        System.out.println("accepterRendezVous avant findById : " + id);
        System.out.println("rdvRepository: " + (rdvRepository != null ? "not null" : "null"));
        System.out.println("id class: " + (id != null ? id.getClass().getName() : "null"));
        System.out.println("id value: " + id);

        try {

            if (id == null) {
                throw new IllegalArgumentException("L'ID du rendez-vous ne peut pas être null");
            }
            // Dans accepterRendezVous()
            RDV rdv = rdvRepository.findRdvByIdValue(id)
                    .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));


            rdv.setStatut(Statut.ACCEPTE);
            rdvRepository.save(rdv);

            notificationService.creerNotificationRDVAccepte(rdv);
        } catch (Exception e) {
            System.out.println("Exception exacte: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            throw e;
        }
    }


    private static final List<String> TOUS_LES_CRENEAUX = Arrays.asList(

         "09:00", "10:00", "11:00", "12:00", "13:00",  "14:00",  "15:00",  "16:00",  "17:00"
    );
    public List<String> getHeuresDisponibles(Long avocatId, LocalDate date) {
        // Vérifier si la date est dans le passé
        if (date.isBefore(LocalDate.now())) {
            return Collections.emptyList(); // Aucun créneau disponible pour les dates passées
        }

        // Vérifier si c'est un week-end (vendredi et samedi au Maroc)
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY) {
            return Collections.emptyList(); // Aucun créneau disponible le week-end
        }

        // Récupérer les rendez-vous existants pour cet avocat à cette date
        List<RDV> rdvsExistants = rdvRepository.findByAvocatIdAndDate(avocatId, date);

        // Extraire les heures déjà réservées
        List<String> heuresOccupees = rdvsExistants.stream()
                .map(RDV::getHeur)
                .collect(Collectors.toList());

        // Si la date est aujourd'hui, filtrer les heures passées
        if (date.isEqual(LocalDate.now())) {
            LocalTime maintenant = LocalTime.now();

            return TOUS_LES_CRENEAUX.stream()
                    .filter(heure -> !heuresOccupees.contains(heure))
                    .filter(heure -> {
                        String[] parts = heure.split(":");
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        LocalTime heureRdv = LocalTime.of(hour, minute);
                        // Ajouter une marge de 30 minutes
                        return heureRdv.isAfter(maintenant.plusMinutes(30));
                    })
                    .collect(Collectors.toList());
        } else {
            // Pour les dates futures, retourner toutes les heures non occupées
            return TOUS_LES_CRENEAUX.stream()
                    .filter(heure -> !heuresOccupees.contains(heure))
                    .collect(Collectors.toList());
        }
    }
    public List<String> getHeuresDisponiblesBureau(Long bureauId, LocalDate date) {
        // Vérifier si la date est dans le passé
        if (date.isBefore(LocalDate.now())) {
            return Collections.emptyList(); // Aucun créneau disponible pour les dates passées
        }

        // Vérifier si c'est un week-end (vendredi et samedi au Maroc)
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return Collections.emptyList(); // Aucun créneau disponible le week-end
        }

        // Récupérer tous les rendez-vous existants pour ce bureau à cette date
        List<RDV> rdvsExistants = rdvRepository.findByBureauIdAndDate(bureauId, date);

        // Filtrer les créneaux selon la règle de disponibilité:
        // 1. Disponible si bureau et avocat sont non null
        // 2. Disponible si bureau et avocat sont null
        // 3. Non disponible si bureau non null mais avocat null
        List<String> heuresOccupees = rdvsExistants.stream()
                .filter(rdv ->
                        // Cas 3: Non disponible si bureau non null mais avocat null
                        (rdv.getBureau() != null && rdv.getAvocat() == null) ||
                                // Les autres cas sont considérés comme occupés normalement
                                !((rdv.getBureau() != null && rdv.getAvocat() != null) ||
                                        (rdv.getBureau() == null && rdv.getAvocat() == null))
                )
                .map(RDV::getHeur)
                .collect(Collectors.toList());

        // Si la date est aujourd'hui, filtrer les heures passées
        if (date.isEqual(LocalDate.now())) {
            LocalTime maintenant = LocalTime.now();

            return TOUS_LES_CRENEAUX.stream()
                    .filter(heure -> !heuresOccupees.contains(heure))
                    .filter(heure -> {
                        String[] parts = heure.split(":");
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        LocalTime heureRdv = LocalTime.of(hour, minute);
                        // Ajouter une marge de 30 minutes
                        return heureRdv.isAfter(maintenant.plusMinutes(30));
                    })
                    .collect(Collectors.toList());
        } else {
            // Pour les dates futures, retourner toutes les heures non occupées
            return TOUS_LES_CRENEAUX.stream()
                    .filter(heure -> !heuresOccupees.contains(heure))
                    .collect(Collectors.toList());
        }
    }


    public Page<RDV> findByBureauAvocat(BureauAvocat bureau, Pageable pageable) {
        return rdvRepository.findByBureau(bureau, pageable);
    }

    public long countTodayAppointmentsByBureau(BureauAvocat bureau) {
        LocalDate today = LocalDate.now();
        return rdvRepository.countByBureauAndDate(bureau, today);
    }
    public List<RDV> findByBureauIdAndDateBetween(Long bureauId, LocalDate startDate, LocalDate endDate) {
        // Vous devrez ajouter cette méthode à votre RdvRepository
        return rdvRepository.findByBureauIdAndDateBetween(bureauId, startDate, endDate);
    }

    public List<RDV> findByBureauIdAndDate(Long bureauId, LocalDate date) {
        // Vous devrez ajouter cette méthode à votre RdvRepository
        return rdvRepository.findByBureauIdAndDate(bureauId, date);
    }

    public void refuserRendezVous(Long id) {
        RDV rdv = rdvRepository.findById(id).orElse(null);
        if (rdv != null) {
            rdv.setStatut(Statut.REFUSE);
            rdvRepository.save(rdv);

            // Créer une notification pour le client
            notificationService.creerNotificationRDVRefuse(rdv);
        }
    }

    @Override
    public List<RDV> findAll() {
        return rdvRepository.findAll();
    }

    @Override
    public RDV findById(Long id) {
        Optional<RDV> rdv = rdvRepository.findById(id);
        return rdv.orElse(null);
    }

    @Override
    public RDV save(RDV rdv) {
        return rdvRepository.save(rdv);
    }

    @Override
    public RDV update(RDV rdv) {
        if (rdv.getId() == null || !rdvRepository.existsById(rdv.getId())) {
            return null;
        }
        return rdvRepository.save(rdv);
    }

    @Override
    public void deleteById(Long id) {
        if (rdvRepository.existsById(id)) {
            rdvRepository.deleteById(id);
        }
    }

    @Override
    public RDV addRdv(RDV rdv, Long clientId, Long bureauId) {
        Client client = clientService.findById(clientId);
        BureauAvocat bureau = bureauAvocatService.findById(bureauId);

        if (client == null || bureau == null) {
            return null;
        }

        rdv.setClient(client);
        rdv.setBureau(bureau);
        return rdvRepository.save(rdv);
    }

    public List<RDV> findByBureauAvocat(BureauAvocat bureau) {
        return rdvRepository.findByBureau(bureau);
    }
    @Override
    public List<RDV> findByClientId(Long clientId) {
        return rdvRepository.findByClientId(clientId);
    }
    public boolean isDateAvailable(Long bureauId, LocalDate date) {
        List<RDV> existingRdvs = rdvRepository.findByBureauIdAndDate(bureauId, date);
        return existingRdvs.isEmpty();
    }


    @Override
    public List<RDV> findByBureauId(Long bureauId) {
        return rdvRepository.findByBureauId(bureauId);
    }




    @Scheduled(cron = "0 0 8 * * *") // Exécuté tous les jours à 8h du matin
    public void envoyerRappelsRendezVous() {
        LocalDate dateRappel = LocalDate.now().plusDays(2);
        List<RDV> rdvsARappeler = rdvRepository.findByDate(dateRappel);

        for (RDV rdv : rdvsARappeler) {
            if (rdv.getClient() != null && rdv.getClient().getEmail() != null) {
                envoyerEmailRappel(rdv);
            }
        }
    }

    private void envoyerEmailRappel(RDV rdv) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(rdv.getClient().getEmail());
        mailMessage.setSubject("Rappel de rendez-vous");

        // Préparez les messages pour le template
        String[] messages = {
                "Bonjour " + rdv.getClient().getFirstName(),
                "Ce message est un rappel de votre rendez-vous prévu le " +
                        rdv.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " à " + rdv.getHeur(),
                "Détails du rendez-vous :",
                "Avocat : " + rdv.getAvocat().getFirstName() + " " + rdv.getAvocat().getLastName(),
                "Sujet : " + rdv.getSujet()
        };

        // Utilisez un URL factice ou un lien vers votre application
        String url = "https://votre-site.com/rendez-vous";

        // Utilisez MailClient pour envoyer l'email
        mailClient.prepareAndSend(
                mailMessage,
                "logo",
                "chemin/vers/image/optionnelle.jpg",
                messages
        );
    }
    public Page<RDV> findByAvocat(Avocat avocat, Pageable pageable) {
        return rdvRepository.findByAvocat(avocat, pageable);
    }

    public Page<RDV> findByAvocatAndStatut(Avocat avocat, Statut statut, Pageable pageable) {
        return rdvRepository.findByAvocatAndStatut(avocat, statut, pageable);
    }
    public long countByAvocat(Avocat avocat) {
        return rdvRepository.countByAvocat(avocat);
    }

    public long countByAvocatAndStatut(Avocat avocat, Statut statut) {
        return rdvRepository.countByAvocatAndStatut(avocat, statut);
    }

    public Optional<RDV> findNextByAvocat(Avocat avocat) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("date").ascending().and(Sort.by("heur").ascending()));
        List<RDV> rdvs = rdvRepository.findNextByAvocat(avocat, pageable);
        return rdvs.isEmpty() ? Optional.empty() : Optional.of(rdvs.get(0));
    }

    /**
     * Rechercher des rendez-vous par sujet ou par client pour un avocat
     */
    public List<RDV> search(Avocat avocat, String query) {
        return rdvRepository.search(avocat, query);
    }

    /**
     * Recherche avancée avec filtres
     */
    public List<RDV> search(Avocat avocat, String query, Statut statut, String fromDateStr, String toDateStr) {
        LocalDate fromDate = null;
        LocalDate toDate = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (fromDateStr != null && !fromDateStr.isEmpty()) {
            try {
                fromDate = LocalDate.parse(fromDateStr, formatter);
            } catch (Exception e) {
                // Gestion d'erreur si la date n'est pas au bon format
            }
        }

        if (toDateStr != null && !toDateStr.isEmpty()) {
            try {
                toDate = LocalDate.parse(toDateStr, formatter);
            } catch (Exception e) {
                // Gestion d'erreur si la date n'est pas au bon format
            }
        }

        return rdvRepository.search(avocat, query, statut, fromDate, toDate);
    }

    /**
     * Supprimer un rendez-vous
     */
    public void delete(RDV rdv) {
        rdvRepository.delete(rdv);
    }
        public long countPendingByAvocat(Avocat avocat) {
            return rdvRepository.countByAvocatAndStatut(avocat, Statut.EN_ATTENTE);
        }



        /**
         * Accepte un rendez-vous
         * @param rdv Le rendez-vous à accepter
         * @return Le rendez-vous mis à jour
         */
        public RDV acceptRdv(RDV rdv) {
            rdv.setStatut(Statut.ACCEPTE);
            return rdvRepository.save(rdv);
        }

        /**
         * Refuse un rendez-vous
         * @param rdv Le rendez-vous à refuser
         * @return Le rendez-vous mis à jour
         */
        public RDV refuseRdv(RDV rdv) {
            rdv.setStatut(Statut.REFUSE);
            return rdvRepository.save(rdv);
        }



        /**
         * Recherche des rendez-vous par sujet ou nom de client
         * @param query Le terme de recherche
         * @param pageable La configuration de pagination
         * @return Une page de rendez-vous correspondant à la recherche
         */
        public Page<RDV> search(String query, Pageable pageable) {
            return rdvRepository.search(query, pageable);
        }

        /**
         * Recherche des rendez-vous d'un avocat spécifique par sujet ou nom de client
         * @param avocat L'avocat pour lequel chercher les rendez-vous
         * @param query Le terme de recherche
         * @param pageable La configuration de pagination
         * @return Une page de rendez-vous correspondant à la recherche
         */
        public Page<RDV> searchByAvocat(Avocat avocat, String query, Pageable pageable) {
            return rdvRepository.searchByAvocat(avocat.getId(), query, pageable);
        }


}
