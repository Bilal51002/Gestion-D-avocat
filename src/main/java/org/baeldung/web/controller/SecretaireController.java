package org.baeldung.web.controller;

import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.pfe.BureauAvocatRepository;
import org.baeldung.persistence.dao.pfe.JugeRepository;
import org.baeldung.persistence.dao.pfe.TribunalRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.*;
import org.baeldung.service.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class SecretaireController {

    private static final Logger logger = LoggerFactory.getLogger(SecretaireController.class);

    @Autowired
    private RdvService rdvService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TribunalRepository tribunalRepository;

    @Autowired
    private JugeRepository jugeRepository;

    @Autowired
    private AvocatService avocatService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BureauAvocatRepository bureauAvocatRepository;

    @Autowired
    private DossierService dossierService;

    @Autowired
    private TypeDossierService typeDossierService;

    @Autowired
    private UserRepository userRepository;

    // Méthodes existantes...

    @GetMapping("/Secretaire/Dashboard")
    @Transactional(readOnly = true)
    public String dashboard(Model model,
                            @RequestParam(name = "lang", required = false) String lang,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "5") int size) {

        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Secretaire secretaire = (Secretaire) auth.getPrincipal();

        // Vérifier que la secrétaire est bien associée à un bureau
        if (secretaire.getBureau() == null) {
            logger.error("La secrétaire {} n'est pas associée à un bureau", secretaire.getEmail());
            return "error";
        }

        // Récupérer le bureau de la secrétaire
        BureauAvocat bureau = secretaire.getBureau();

        // Récupérer tous les dossiers associés au bureau
        List<Dossier> dossiers = dossierService.getDossiersByBureau(bureau);

        // Initialiser les collections client des dossiers
        for (Dossier dossier : dossiers) {
            Hibernate.initialize(dossier.getClient());
        }

        model.addAttribute("dossiers", dossiers);
        model.addAttribute("totalDossiers", dossiers.size());

        // Compter les dossiers avec une session prochaine
        long dossiersAvecSessionProchaine = dossiers.stream()
                .filter(d -> d.getDateProchSession() != null)
                .count();
        model.addAttribute("dossiersAvecSessionProchaine", dossiersAvecSessionProchaine);

        // Code existant pour la pagination des RDV
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<RDV> rdvPage = rdvService.findByBureauAvocat(bureau, pageable);
        model.addAttribute("rdvPage", rdvPage);

        // Autres attributs du modèle
        model.addAttribute("secretaire", secretaire);
        List<RDV> rdvs = rdvService.findAll();
        model.addAttribute("rdvs", rdvs);

        // Notifications
        List<Notification> notifications = notificationService.getNotificationsByUser(secretaire);
        model.addAttribute("notifications", notifications);

        long unreadNotificationsCount = notifications.stream()
                .filter(notification -> !notification.isLu())
                .count();
        model.addAttribute("unreadNotificationsCount", unreadNotificationsCount);

        // Générer les numéros de page
        if (rdvPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, rdvPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Compter les rendez-vous du jour pour ce bureau
        long todayAppointmentsCount = rdvService.countTodayAppointmentsByBureau(bureau);
        model.addAttribute("todayAppointmentsCount", todayAppointmentsCount);

        return "Secretaire/Dashboard";
    }

    @GetMapping("/Secretaire/notifications")
    public String listerNotifications(Model model) {
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Ajouter les notifications au modèle
        model.addAttribute("notifications", notificationService.getNotificationsByUser(currentUser));
        return "Secretaire/notifications";
    }
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // Get the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Add unread notifications count
        long unreadCount = notificationService.countNonLues(currentUser);
        model.addAttribute("unreadNotificationsCount", unreadCount);
    }

    @GetMapping("/Secretaire/notifications/{id}")
    public String afficherNotification(@PathVariable Long id, Model model) {
        Notification notification = notificationService.getNotificationById(id);
        if (notification != null) {
            notificationService.marquerCommeLue(id);
            model.addAttribute("notification", notification);

            // Si c'est une notification de rendez-vous, on ajoute le RDV au modèle
            if (notification.getType().startsWith("RDV_")) {
                model.addAttribute("rdv", notification.getRendezVous());
            }

            return "Secretaire/notification-details";
        }
        return "redirect:/Secretaire/Dashboard";
    }

    // Accepter un rendez-vous
    @PostMapping("/Secretaire/rdv/accepter/{id}")
    public String accepterRendezVous(@PathVariable Long id) {
        System.out.println("id : " + id);
        rdvService.accepterRendezVous(id);
        return "redirect:/Secretaire/notifications";
    }

    // Refuser un rendez-vous
    @PostMapping("/Secretaire/rdv/refuser/{id}")
    public String refuserRendezVous(@PathVariable Long id) {
        rdvService.refuserRendezVous(id);
        return "redirect:/Secretaire/notifications";
    }

    @GetMapping(value = "Secretaire/dossier/edit/{id}")
    @Transactional(readOnly = true)
    public String showEditDossierForm(@PathVariable("id") Long id, Model model) {
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Secretaire secretaire = (Secretaire) auth.getPrincipal();

        // Récupérer le dossier
        Dossier dossier = dossierService.getDossierById(id);

        // Vérifier que le dossier existe
        if (dossier == null) {
            return "redirect:/Secretaire/dossier/list";
        }

        // Vérifier que le dossier appartient au bureau de la secrétaire
        if (!dossier.getBureau().getId().equals(secretaire.getBureau().getId())) {
            return "redirect:/Secretaire/dossier/list";
        }

        // Initialiser explicitement les collections pour éviter les LazyInitializationException
        Hibernate.initialize(dossier.getClient());
        Hibernate.initialize(dossier.getAvocat());
        Hibernate.initialize(dossier.getTribunal());
        Hibernate.initialize(dossier.getJuge());

        model.addAttribute("secretaire", secretaire);
        model.addAttribute("dossier", dossier);
        model.addAttribute("typeDossiers", typeDossierService.findAll());

        // Préparer les listes des entités liées
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("avocats", avocatService.findAll());
        model.addAttribute("tribunaux", tribunalRepository.findAll());
        model.addAttribute("juges", jugeRepository.findAll());

        // Préparer les listes d'IDs sélectionnés pour faciliter le binding dans le formulaire
        List<Long> selectedClientIds = dossier.getClient().stream()
                .map(Client::getId)
                .collect(Collectors.toList());
        model.addAttribute("selectedClientIds", selectedClientIds);

        List<Long> selectedAvocatIds = dossier.getAvocat().stream()
                .map(Avocat::getId)
                .collect(Collectors.toList());
        model.addAttribute("selectedAvocatIds", selectedAvocatIds);

        List<Long> selectedTribunalIds = dossier.getTribunal().stream()
                .map(Tribunal::getId)
                .collect(Collectors.toList());
        model.addAttribute("selectedTribunalIds", selectedTribunalIds);

        List<Long> selectedJugeIds = dossier.getJuge().stream()
                .map(Juge::getId)
                .collect(Collectors.toList());
        model.addAttribute("selectedJugeIds", selectedJugeIds);

        return "Secretaire/dossier/edit";
    }

    @PostMapping(value = "Secretaire/dossier/edit/{id}")
    @Transactional
    public String updateDossier(@PathVariable("id") Long id,
                                @ModelAttribute Dossier dossierForm,
                                @RequestParam(value = "clientIds", required = false) List<Long> clientIds,
                                @RequestParam(value = "avocatIds", required = false) List<Long> avocatIds,
                                @RequestParam(value = "tribunalIds", required = false) List<Long> tribunalIds,
                                @RequestParam(value = "jugeIds", required = false) List<Long> jugeIds,
                                RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Secretaire secretaire = (Secretaire) auth.getPrincipal();

            // Récupérer le dossier original
            Dossier dossier = dossierService.getDossierById(id);

            // Vérifier que le dossier existe
            if (dossier == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Dossier non trouvé");
                return "redirect:/Secretaire/dossier/list";
            }

            // Vérifier que le dossier appartient au bureau de la secrétaire
            if (!dossier.getBureau().getId().equals(secretaire.getBureau().getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vous n'êtes pas autorisé à modifier ce dossier");
                return "redirect:/Secretaire/dossier/list";
            }

            // Mise à jour des champs simples
            dossier.setTypeDecas(dossierForm.getTypeDecas());
            dossier.setSujet(dossierForm.getSujet());
            dossier.setTypeProsedure(dossierForm.getTypeProsedure());
            dossier.setDateProchSession(dossierForm.getDateProchSession());
            dossier.setNumeroNational(dossierForm.getNumeroNational());
            dossier.setTypeD(dossierForm.getTypeD());

            // Mise à jour des clients
            Collection<Client> nouveauxClients = new ArrayList<>();
            if (clientIds != null && !clientIds.isEmpty()) {
                for (Long clientId : clientIds) {
                    Client client = clientService.findById(clientId);
                    if (client != null) {
                        nouveauxClients.add(client);

                        // Mise à jour bidirectionnelle
                        if (client.getDossiers() == null) {
                            client.setDossiers(new ArrayList<>());
                        }
                        if (!client.getDossiers().contains(dossier)) {
                            client.getDossiers().add(dossier);
                        }
                    }
                }

                // Retirer le dossier des clients qui ne sont plus associés
                for (Client client : new ArrayList<>(dossier.getClient())) {
                    if (!nouveauxClients.contains(client)) {
                        client.getDossiers().remove(dossier);
                    }
                }
            } else {
                // Si aucun client n'est sélectionné, vider la collection
                for (Client client : new ArrayList<>(dossier.getClient())) {
                    client.getDossiers().remove(dossier);
                }
            }
            dossier.setClient(nouveauxClients);

            // Mise à jour des avocats
            Collection<Avocat> nouveauxAvocats = new ArrayList<>();
            if (avocatIds != null && !avocatIds.isEmpty()) {
                for (Long avocatId : avocatIds) {
                    Avocat avocat = avocatService.findById(avocatId);
                    if (avocat != null) {
                        nouveauxAvocats.add(avocat);
                    }
                }
            }
            dossier.setAvocat(nouveauxAvocats);

            // Mise à jour des tribunaux
            Collection<Tribunal> nouveauxTribunaux = new ArrayList<>();
            if (tribunalIds != null && !tribunalIds.isEmpty()) {
                for (Long tribunalId : tribunalIds) {
                    tribunalRepository.findById(tribunalId).ifPresent(nouveauxTribunaux::add);
                }
            }
            dossier.setTribunal(nouveauxTribunaux);

            // Mise à jour des juges
            Collection<Juge> nouveauxJuges = new ArrayList<>();
            if (jugeIds != null && !jugeIds.isEmpty()) {
                for (Long jugeId : jugeIds) {
                    jugeRepository.findById(jugeId).ifPresent(nouveauxJuges::add);
                }
            }
            dossier.setJuge(nouveauxJuges);

            // Enregistrer le dossier mis à jour
            dossierService.addDossier(dossier);

            redirectAttributes.addFlashAttribute("successMessage", "Dossier mis à jour avec succès");
            return "redirect:/Secretaire/dossier/details/" + id;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du dossier", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du dossier: " + e.getMessage());
            return "redirect:/Secretaire/dossier/edit/" + id;
        }
    }

    @GetMapping(value = "Secretaire/dossier/details/{id}")
    @Transactional(readOnly = true)
    public String dossierDetails(@PathVariable("id") Long id, Model model) {
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userConnected = auth.getPrincipal();

        // Récupérer le dossier
        Dossier dossier = dossierService.getDossierById(id);

        // Vérifier que le dossier existe
        if (dossier == null) {
            return "redirect:/Secretaire/Dashboard";
        }

        // Initialiser explicitement les collections pour éviter les LazyInitializationException
        Hibernate.initialize(dossier.getClient());
        Hibernate.initialize(dossier.getAvocat());
        Hibernate.initialize(dossier.getTribunal());
        Hibernate.initialize(dossier.getJuge());

        // Ajouter le dossier au modèle
        model.addAttribute("dossier", dossier);

        // Ajouter l'utilisateur connecté au modèle selon son type
        if (userConnected instanceof Secretaire) {
            model.addAttribute("secretaire", userConnected);
        } else if (userConnected instanceof Client) {
            model.addAttribute("client", userConnected);
        }

        return "Secretaire/dossier/details";
    }

    @GetMapping(value = "Secretaire/dossier/create")
    public String showCreateDossierForm(Model model) {
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Secretaire secretaire = (Secretaire) auth.getPrincipal();

        // Récupérer le bureau de la secrétaire
        BureauAvocat bureau = secretaire.getBureau();

        model.addAttribute("secretaire", secretaire);
        model.addAttribute("dossier", new Dossier());
        model.addAttribute("typeDossiers", typeDossierService.findAll());

        // Ajouter les clients, avocats, tribunaux et juges au modèle
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("avocats", avocatService.findAll());
        model.addAttribute("tribunaux", tribunalRepository.findAll());
        model.addAttribute("juges", jugeRepository.findAll());

        return "Secretaire/dossier/create";
    }

    @PostMapping(value = "Secretaire/dossier/create")
    @Transactional
    public String createDossier(@ModelAttribute Dossier dossier,
                                @RequestParam(value = "clientIds", required = false) List<Long> clientIds,
                                @RequestParam(value = "avocatIds", required = false) List<Long> avocatIds,
                                @RequestParam(value = "tribunalIds", required = false) List<Long> tribunalIds,
                                @RequestParam(value = "jugeIds", required = false) List<Long> jugeIds,
                                RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Secretaire secretaire = (Secretaire) auth.getPrincipal();

            // Récupérer le bureau de la secrétaire
            BureauAvocat bureau = secretaire.getBureau();

            // Générer un numéro de dossier unique
            String numeroDossier = "LD-" + java.time.LocalDate.now().getYear() + "-" +
                    String.format("%04d", (int)(Math.random() * 10000));
            dossier.setNumeroDossier(numeroDossier);

            // Définir la date de création
            dossier.setDateCreation(new java.sql.Date(System.currentTimeMillis()));

            // Associer le bureau d'avocat
            dossier.setBureau(bureau);

            // Associer les clients au dossier
            if (clientIds != null && !clientIds.isEmpty()) {
                Collection<Client> clients = new ArrayList<>();
                for (Long id : clientIds) {
                    Client client = clientService.findById(id);
                    if (client != null) {
                        clients.add(client);

                        // Mise à jour bidirectionnelle - AJOUT IMPORTANT
                        if (client.getDossiers() == null) {
                            client.setDossiers(new ArrayList<>());
                        }
                        client.getDossiers().add(dossier);
                    }
                }
                dossier.setClient(clients);
            }

            // Associer les avocats au dossier
            if (avocatIds != null && !avocatIds.isEmpty()) {
                Collection<Avocat> avocats = new ArrayList<>();
                for (Long id : avocatIds) {
                    Avocat avocat = avocatService.findById(id);
                    if (avocat != null) {
                        avocats.add(avocat);
                    }
                }
                dossier.setAvocat(avocats);
            }

            // Associer les tribunaux au dossier
            if (tribunalIds != null && !tribunalIds.isEmpty()) {
                Collection<Tribunal> tribunaux = new ArrayList<>();
                for (Long id : tribunalIds) {
                    tribunalRepository.findById(id).ifPresent(tribunaux::add);
                }
                dossier.setTribunal(tribunaux);
            }

            // Associer les juges au dossier
            if (jugeIds != null && !jugeIds.isEmpty()) {
                Collection<Juge> juges = new ArrayList<>();
                for (Long id : jugeIds) {
                    jugeRepository.findById(id).ifPresent(juges::add);
                }
                dossier.setJuge(juges);
            }

            // Enregistrer le dossier
            dossierService.addDossier(dossier);

            redirectAttributes.addFlashAttribute("successMessage", "Dossier créé avec succès");

            return "redirect:/Secretaire/dossier/list";
        } catch (Exception e) {
            logger.error("Erreur lors de la création du dossier", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la création du dossier: " + e.getMessage());
            return "redirect:/Secretaire/dossier/create";
        }
    }

    @GetMapping("/Secretaire/dossier/list")
    @Transactional(readOnly = true)
    public String listDossiers(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size) {
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Secretaire secretaire = (Secretaire) auth.getPrincipal();

        // Vérifier que la secrétaire est bien associée à un bureau
        if (secretaire.getBureau() == null) {
            logger.error("La secrétaire {} n'est pas associée à un bureau", secretaire.getEmail());
            return "error";
        }

        // Récupérer le bureau de la secrétaire
        BureauAvocat bureau = secretaire.getBureau();

        // Paginer les dossiers - CORRECTION: "dateCreation" avec d minuscule
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<Dossier> dossierPage = dossierService.getDossiersByBureauPaginated(bureau, pageable);

        // CRUCIAL: Initialiser la collection client pour chaque dossier
        for (Dossier dossier : dossierPage.getContent()) {
            Hibernate.initialize(dossier.getClient());
        }

        // Si la page demandée n'existe pas, revenir à la première page
        if (page > 0 && page >= dossierPage.getTotalPages()) {
            return "redirect:/Secretaire/dossier/list?page=0";
        }

        model.addAttribute("dossierPage", dossierPage);
        model.addAttribute("dossiers", dossierPage.getContent());
        model.addAttribute("secretaire", secretaire);
        model.addAttribute("typeDossiers", typeDossierService.findAll());

        return "Secretaire/dossier/list";
    }
}