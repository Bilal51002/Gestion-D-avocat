package org.baeldung.web.controller;

import org.baeldung.persistence.dao.pfe.AvocatRepository;
import org.baeldung.persistence.dao.pfe.JugeRepository;
import org.baeldung.persistence.dao.pfe.TribunalRepository;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Avocat")
public class AvocatController {

    private static final Logger logger = LoggerFactory.getLogger(AvocatController.class);

    @Autowired
    private DossierService dossierService;

    @Autowired
    private RdvService rdvService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BureauAvocatService bureauAvocatService;

    @Autowired
    private TribunalRepository tribunalRepository;

    @Autowired
    private JugeRepository jugeRepository;

    @Autowired
    private TypeDossierService typeDossierService;

    @Autowired
    private AvocatRepository avocatRepository;

    @GetMapping("/Dashboard")
    @Transactional
    public String Dashboard(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int appointmentPage,
            @RequestParam(defaultValue = "0") int clientPage,
            @RequestParam(defaultValue = "0") int hearingPage,
            @RequestParam(defaultValue = "0") int notificationPage,
            @RequestParam(defaultValue = "0") int bureauPage) {

        // Récupération de l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();
        String email = avocat.getEmail();

        if (avocat == null) {
            System.out.println("Aucun avocat trouvé pour l'email: " + email);
            return "redirect:/login"; // Rediriger vers la page de connexion
        }

        model.addAttribute("avocat", avocat);

        try {
            // Statistiques pour le dashboard
            long totalDossiers = dossierService.countByAvocat(avocat);
            long activeDossiers = dossierService.countActiveByAvocat(avocat);
            long pendingAppointments = rdvService.countPendingByAvocat(avocat);
            long totalClients = clientService.countByAvocat(avocat);

            model.addAttribute("totalDossiers", totalDossiers);
            model.addAttribute("activeDossiers", activeDossiers);
            model.addAttribute("pendingAppointments", pendingAppointments);
            model.addAttribute("totalClients", totalClients);

            // Pagination pour les dossiers
            Pageable dossierPageable = PageRequest.of(page, 10, Sort.by("dateCreation").descending());
            Page<Dossier> dossierPage = dossierService.findByAvocat(avocat, dossierPageable);
            model.addAttribute("dossiers", dossierPage.getContent());
            model.addAttribute("currentDossierPage", dossierPage.getNumber() + 1);
            model.addAttribute("totalDossierPages", dossierPage.getTotalPages());

            // Pagination pour les rendez-vous
            Pageable rdvPageable = PageRequest.of(appointmentPage, 10, Sort.by("date").ascending());
            Page<RDV> rdvPage = rdvService.findByAvocat(avocat, rdvPageable);
            model.addAttribute("appointments", rdvPage.getContent());
            model.addAttribute("currentAppointmentPage", rdvPage.getNumber() + 1);
            model.addAttribute("totalAppointmentPages", rdvPage.getTotalPages());

            // Pagination pour les clients
            Pageable clientPageable = PageRequest.of(clientPage, 10, Sort.by("firstName").ascending());
            Page<Client> clientsPage = clientService.findByAvocat(avocat, clientPageable); // Renommée en clientsPage
            model.addAttribute("clients", clientsPage.getContent());
            model.addAttribute("currentClientPage", clientsPage.getNumber() + 1);
            model.addAttribute("totalClientPages", clientsPage.getTotalPages());

            // Pagination pour les bureaux
            Pageable bureauPageable = PageRequest.of(bureauPage, 10, Sort.by("nom").ascending());
            Page<BureauAvocat> bureauAvocatsPage = bureauAvocatService.findAll(bureauPageable); // Renommée en bureauAvocatsPage
            model.addAttribute("bureaux", bureauAvocatsPage.getContent());
            model.addAttribute("currentBureauPage", bureauAvocatsPage.getNumber() + 1);
            model.addAttribute("totalBureauPages", bureauAvocatsPage.getTotalPages());

            // Récupération du prochain rendez-vous
            Optional<RDV> nextAppointment = rdvService.findNextByAvocat(avocat);
            nextAppointment.ifPresent(rdv -> model.addAttribute("nextAppointment", rdv));

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des données: " + e.getMessage());
            e.printStackTrace();
        }

        return "Avocat/Dashboard";
    }

    @GetMapping("/dossier/edit/{id}")
    @Transactional(readOnly = true)
    public String showEditDossierForm(@PathVariable("id") Long id, Model model) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Récupérer le dossier
        Dossier dossier = dossierService.getDossierById(id);

        // Vérifier que le dossier existe
        if (dossier == null) {
            return "redirect:/Avocat/dossier/list";
        }

        // Vérifier que l'avocat est associé au dossier
        if (!dossier.getAvocat().contains(avocat)) {
            return "redirect:/Avocat/dossier/list";
        }

        // Initialiser explicitement les collections pour éviter les LazyInitializationException
        Hibernate.initialize(dossier.getClient());
        Hibernate.initialize(dossier.getAvocat());
        Hibernate.initialize(dossier.getTribunal());
        Hibernate.initialize(dossier.getJuge());

        model.addAttribute("avocat", avocat);
        model.addAttribute("dossier", dossier);
        model.addAttribute("typeDossiers", typeDossierService.findAll());

        // Préparer les listes des entités liées
        // Créez une méthode dans ClientService qui renvoie une liste non paginée
        model.addAttribute("clients", clientService.findAllByAvocat(avocat));
        model.addAttribute("tribunaux", tribunalRepository.findAll());
        model.addAttribute("juges", jugeRepository.findAll());

        // Préparer les listes d'IDs sélectionnés pour faciliter le binding dans le formulaire
        List<Long> selectedClientIds = dossier.getClient().stream()
                .map(Client::getId)
                .collect(Collectors.toList());
        model.addAttribute("selectedClientIds", selectedClientIds);

        List<Long> selectedTribunalIds = dossier.getTribunal().stream()
                .map(Tribunal::getId)
                .collect(Collectors.toList());
        model.addAttribute("selectedTribunalIds", selectedTribunalIds);

        List<Long> selectedJugeIds = dossier.getJuge().stream()
                .map(Juge::getId)
                .collect(Collectors.toList());
        model.addAttribute("selectedJugeIds", selectedJugeIds);

        return "Avocat/dossier/edit";
    }

    @PostMapping("/dossier/edit/{id}")
    @Transactional
    public String updateDossier(@PathVariable("id") Long id,
                                @ModelAttribute Dossier dossierForm,
                                @RequestParam(value = "clientIds", required = false) List<Long> clientIds,
                                @RequestParam(value = "tribunalIds", required = false) List<Long> tribunalIds,
                                @RequestParam(value = "jugeIds", required = false) List<Long> jugeIds,
                                RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'avocat connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Avocat avocat = (Avocat) auth.getPrincipal();

            // Récupérer le dossier original
            Dossier dossier = dossierService.getDossierById(id);

            // Vérifier que le dossier existe
            if (dossier == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Dossier non trouvé");
                return "redirect:/Avocat/dossier/list";
            }

            // Vérifier que l'avocat est associé au dossier
            if (!dossier.getAvocat().contains(avocat)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vous n'êtes pas autorisé à modifier ce dossier");
                return "redirect:/Avocat/dossier/list";
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

            // Pour un avocat, on ne modifie pas la liste des avocats associés au dossier
            // Conserver l'avocat actuellement connecté dans la liste
            if (!dossier.getAvocat().contains(avocat)) {
                dossier.getAvocat().add(avocat);
            }

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
            return "redirect:/Avocat/dossier/details/" + id;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du dossier", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du dossier: " + e.getMessage());
            return "redirect:/Avocat/dossier/edit/" + id;
        }
    }

    @GetMapping("/dossier/details/{id}")
    @Transactional(readOnly = true)
    public String dossierDetails(@PathVariable("id") Long id, Model model) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Récupérer le dossier
        Dossier dossier = dossierService.getDossierById(id);

        // Vérifier que le dossier existe
        if (dossier == null) {
            return "redirect:/Avocat/Dashboard";
        }

        // Vérifier que l'avocat est associé au dossier
        if (!dossier.getAvocat().contains(avocat)) {
            return "redirect:/Avocat/Dashboard";
        }

        // Initialiser explicitement les collections pour éviter les LazyInitializationException
        Hibernate.initialize(dossier.getClient());
        Hibernate.initialize(dossier.getAvocat());
        Hibernate.initialize(dossier.getTribunal());
        Hibernate.initialize(dossier.getJuge());

        // Ajouter le dossier au modèle
        model.addAttribute("dossier", dossier);
        model.addAttribute("avocat", avocat);

        return "Avocat/dossier/details";
    }

    @GetMapping("/dossier/create")
    public String showCreateDossierForm(Model model) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        model.addAttribute("avocat", avocat);
        model.addAttribute("dossier", new Dossier());
        model.addAttribute("typeDossiers", typeDossierService.findAll());

        // Ajouter les clients, tribunaux et juges au modèle
        model.addAttribute("clients", clientService.findAllByAvocat(avocat));
        model.addAttribute("tribunaux", tribunalRepository.findAll());
        model.addAttribute("juges", jugeRepository.findAll());

        return "Avocat/dossier/create";
    }

    @PostMapping("/dossier/create")
    @Transactional
    public String createDossier(@ModelAttribute Dossier dossier,
                                @RequestParam(value = "clientIds", required = false) List<Long> clientIds,
                                @RequestParam(value = "tribunalIds", required = false) List<Long> tribunalIds,
                                @RequestParam(value = "jugeIds", required = false) List<Long> jugeIds,
                                RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'avocat connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Avocat avocat = (Avocat) auth.getPrincipal();

            // Générer un numéro de dossier unique
            String numeroDossier = "LD-" + java.time.LocalDate.now().getYear() + "-" +
                    String.format("%04d", (int)(Math.random() * 10000));
            dossier.setNumeroDossier(numeroDossier);

            // Définir la date de création
            dossier.setDateCreation(new java.sql.Date(System.currentTimeMillis()));

            // Associer le bureau d'avocat si l'avocat est rattaché à un bureau
            if (avocat.getBureau() != null) {
                dossier.setBureau(avocat.getBureau());
            }

            // Associer l'avocat connecté au dossier
            Collection<Avocat> avocats = new ArrayList<>();
            avocats.add(avocat);
            dossier.setAvocat(avocats);

            // Associer les clients au dossier
            if (clientIds != null && !clientIds.isEmpty()) {
                Collection<Client> clients = new ArrayList<>();
                for (Long id : clientIds) {
                    Client client = clientService.findById(id);
                    if (client != null) {
                        clients.add(client);

                        // Mise à jour bidirectionnelle
                        if (client.getDossiers() == null) {
                            client.setDossiers(new ArrayList<>());
                        }
                        client.getDossiers().add(dossier);
                    }
                }
                dossier.setClient(clients);
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

            return "redirect:/Avocat/dossier/list";
        } catch (Exception e) {
            logger.error("Erreur lors de la création du dossier", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la création du dossier: " + e.getMessage());
            return "redirect:/Avocat/dossier/create";
        }
    }

    @GetMapping("/dossier/list")
    @Transactional(readOnly = true)
    public String listDossiers(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Paginer les dossiers associés à l'avocat
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<Dossier> dossierPage = dossierService.findByAvocat(avocat, pageable);

        // Initialiser la collection client pour chaque dossier
        for (Dossier dossier : dossierPage.getContent()) {
            Hibernate.initialize(dossier.getClient());
        }

        // Si la page demandée n'existe pas, revenir à la première page
        if (page > 0 && page >= dossierPage.getTotalPages()) {
            return "redirect:/Avocat/dossier/list?page=0";
        }

        model.addAttribute("dossierPage", dossierPage);
        model.addAttribute("dossiers", dossierPage.getContent());
        model.addAttribute("avocat", avocat);
        model.addAttribute("typeDossiers", typeDossierService.findAll());

        return "Avocat/dossier/list";
    }



    @GetMapping("/rdv/list")
    @Transactional(readOnly = true)
    public String listRdvs(Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size,
                           @RequestParam(name = "statut", required = false) String statutStr) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Pagination pour les rendez-vous
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date").and(Sort.by("heur")));
        Page<RDV> rdvPage;

        // Filtrer par statut si spécifié
        if (statutStr != null && !statutStr.isEmpty()) {
            try {
                Statut statut = Statut.valueOf(statutStr);
                rdvPage = rdvService.findByAvocatAndStatut(avocat, statut, pageable);
            } catch (IllegalArgumentException e) {
                rdvPage = rdvService.findByAvocat(avocat, pageable);
            }
        } else {
            rdvPage = rdvService.findByAvocat(avocat, pageable);
        }

        // Si la page demandée n'existe pas, revenir à la première page
        if (page > 0 && page >= rdvPage.getTotalPages()) {
            return "redirect:/Avocat/rdv/list?page=0" + (statutStr != null ? "&statut=" + statutStr : "");
        }

        model.addAttribute("rdvPage", rdvPage);
        model.addAttribute("appointments", rdvPage.getContent());
        model.addAttribute("currentPage", rdvPage.getNumber() + 1);
        model.addAttribute("totalPages", rdvPage.getTotalPages());
        model.addAttribute("avocat", avocat);
        model.addAttribute("selectedStatut", statutStr);
        model.addAttribute("statuts", Statut.values());

        return "Avocat/rdv/list";
    }

    @GetMapping("/rdv/create")
    public String showCreateRdvForm(Model model,
                                    @RequestParam(name = "dossier", required = false) Long dossierId) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Créer un nouvel objet RDV
        RDV rdv = new RDV();
        rdv.setAvocat(avocat);
        rdv.setBureau(avocat.getBureau());

        // Si un dossierId est fourni, on peut pré-remplir d'autres informations
        if (dossierId != null) {
            // Logique pour pré-remplir les données du rendez-vous basées sur le dossier
        }

        model.addAttribute("rdv", rdv);
        model.addAttribute("avocat", avocat);
        model.addAttribute("clients", clientService.findAllByAvocat(avocat));
        model.addAttribute("dateMin", LocalDate.now());

        return "Avocat/rdv/create";
    }

    @PostMapping("/rdv/create")
    @Transactional
    public String createRdv(@ModelAttribute RDV rdv,
                            @RequestParam("clientId") Long clientId,
                            RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'avocat connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Avocat avocat = (Avocat) auth.getPrincipal();

            // Récupérer le client
            Client client = clientService.findById(clientId);
            if (client == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "العميل غير موجود");
                return "redirect:/Avocat/rdv/create";
            }

            // Définir l'avocat et le client pour le rendez-vous
            rdv.setAvocat(avocat);
            rdv.setClient(client);
            rdv.setBureau(avocat.getBureau());
            rdv.setStatut(Statut.EN_ATTENTE);

            // Vérifier la disponibilité du créneau
            List<String> heuresDisponibles = rdvService.getHeuresDisponibles(avocat.getId(), rdv.getDate());
            if (!heuresDisponibles.contains(rdv.getHeur())) {
                redirectAttributes.addFlashAttribute("errorMessage", "هذا الوقت غير متاح. الرجاء اختيار وقت آخر.");
                return "redirect:/Avocat/rdv/create";
            }

            // Enregistrer le rendez-vous
            rdvService.save(rdv);

            redirectAttributes.addFlashAttribute("successMessage", "تم إنشاء الموعد بنجاح");
            return "redirect:/Avocat/rdv/list";
        } catch (Exception e) {
            logger.error("Erreur lors de la création du rendez-vous", e);
            redirectAttributes.addFlashAttribute("errorMessage", "حدث خطأ أثناء إنشاء الموعد: " + e.getMessage());
            return "redirect:/Avocat/rdv/create";
        }
    }

    @GetMapping("/rdv/details/{id}")
    @Transactional(readOnly = true)
    public String rdvDetails(@PathVariable("id") Long id, Model model) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Récupérer le rendez-vous
        RDV rdv = rdvService.findById(id);

        // Vérifier que le rendez-vous existe
        if (rdv == null) {
            return "redirect:/Avocat/rdv/list";
        }

        // Vérifier que l'avocat est associé au rendez-vous
        if (!rdv.getAvocat().equals(avocat)) {
            return "redirect:/Avocat/rdv/list";
        }

        model.addAttribute("rdv", rdv);
        model.addAttribute("avocat", avocat);

        return "Avocat/rdv/details";
    }

    @GetMapping("/rdv/edit/{id}")
    @Transactional(readOnly = true)
    public String showEditRdvForm(@PathVariable("id") Long id, Model model) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Récupérer le rendez-vous
        RDV rdv = rdvService.findById(id);

        // Vérifier que le rendez-vous existe
        if (rdv == null) {
            return "redirect:/Avocat/rdv/list";
        }

        // Vérifier que l'avocat est associé au rendez-vous
        if (!rdv.getAvocat().equals(avocat)) {
            return "redirect:/Avocat/rdv/list";
        }

        model.addAttribute("rdv", rdv);
        model.addAttribute("avocat", avocat);
        model.addAttribute("clients", clientService.findAllByAvocat(avocat));
        model.addAttribute("dateMin", LocalDate.now());
        model.addAttribute("statuts", Statut.values());

        return "Avocat/rdv/edit";
    }

    @PostMapping("/rdv/edit/{id}")
    @Transactional
    public String updateRdv(@PathVariable("id") Long id,
                            @ModelAttribute RDV rdvForm,
                            @RequestParam("clientId") Long clientId,
                            RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'avocat connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Avocat avocat = (Avocat) auth.getPrincipal();

            // Récupérer le rendez-vous original
            RDV rdv = rdvService.findById(id);

            // Vérifier que le rendez-vous existe
            if (rdv == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "الموعد غير موجود");
                return "redirect:/Avocat/rdv/list";
            }

            // Vérifier que l'avocat est associé au rendez-vous
            if (!rdv.getAvocat().equals(avocat)) {
                redirectAttributes.addFlashAttribute("errorMessage", "لا يمكنك تعديل هذا الموعد");
                return "redirect:/Avocat/rdv/list";
            }

            // Récupérer le client
            Client client = clientService.findById(clientId);
            if (client == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "العميل غير موجود");
                return "redirect:/Avocat/rdv/edit/" + id;
            }

            // Si la date ou l'heure a changé, vérifier la disponibilité
            if (!rdv.getDate().equals(rdvForm.getDate()) || !rdv.getHeur().equals(rdvForm.getHeur())) {
                List<String> heuresDisponibles = rdvService.getHeuresDisponibles(avocat.getId(), rdvForm.getDate());
                if (!heuresDisponibles.contains(rdvForm.getHeur())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "هذا الوقت غير متاح. الرجاء اختيار وقت آخر.");
                    return "redirect:/Avocat/rdv/edit/" + id;
                }
            }

            // Mise à jour des champs
            rdv.setDate(rdvForm.getDate());
            rdv.setHeur(rdvForm.getHeur());
            rdv.setSujet(rdvForm.getSujet());
            rdv.setClient(client);
            rdv.setStatut(rdvForm.getStatut());

            // Enregistrer le rendez-vous mis à jour
            rdvService.save(rdv);

            redirectAttributes.addFlashAttribute("successMessage", "تم تحديث الموعد بنجاح");
            return "redirect:/Avocat/rdv/details/" + id;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du rendez-vous", e);
            redirectAttributes.addFlashAttribute("errorMessage", "حدث خطأ أثناء تحديث الموعد: " + e.getMessage());
            return "redirect:/Avocat/rdv/edit/" + id;
        }
    }

    @GetMapping("/rdv/accept/{id}")
    @Transactional
    public String acceptRdv(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'avocat connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Avocat avocat = (Avocat) auth.getPrincipal();

            // Récupérer le rendez-vous
            RDV rdv = rdvService.findById(id);

            // Vérifier que le rendez-vous existe
            if (rdv == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "الموعد غير موجود");
                return "redirect:/Avocat/rdv/list";
            }

            // Vérifier que l'avocat est associé au rendez-vous
            if (!rdv.getAvocat().equals(avocat)) {
                redirectAttributes.addFlashAttribute("errorMessage", "لا يمكنك قبول هذا الموعد");
                return "redirect:/Avocat/rdv/list";
            }

            // Vérifier que le rendez-vous est en attente
            if (rdv.getStatut() != Statut.EN_ATTENTE) {
                redirectAttributes.addFlashAttribute("errorMessage", "يمكن قبول المواعيد المعلقة فقط");
                return "redirect:/Avocat/rdv/details/" + id;
            }

            // Accepter le rendez-vous
            rdv.setStatut(Statut.ACCEPTE);
            rdvService.save(rdv);

            redirectAttributes.addFlashAttribute("successMessage", "تم قبول الموعد بنجاح");
            return "redirect:/Avocat/rdv/details/" + id;
        } catch (Exception e) {
            logger.error("Erreur lors de l'acceptation du rendez-vous", e);
            redirectAttributes.addFlashAttribute("errorMessage", "حدث خطأ أثناء قبول الموعد: " + e.getMessage());
            return "redirect:/Avocat/rdv/list";
        }
    }

    @GetMapping("/rdv/refuse/{id}")
    @Transactional
    public String refuseRdv(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Récupérer l'avocat connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Avocat avocat = (Avocat) auth.getPrincipal();

            // Récupérer le rendez-vous
            RDV rdv = rdvService.findById(id);

            // Vérifier que le rendez-vous existe
            if (rdv == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "الموعد غير موجود");
                return "redirect:/Avocat/rdv/list";
            }

            // Vérifier que l'avocat est associé au rendez-vous
            if (!rdv.getAvocat().equals(avocat)) {
                redirectAttributes.addFlashAttribute("errorMessage", "لا يمكنك رفض هذا الموعد");
                return "redirect:/Avocat/rdv/list";
            }

            // Vérifier que le rendez-vous est en attente
            if (rdv.getStatut() != Statut.EN_ATTENTE) {
                redirectAttributes.addFlashAttribute("errorMessage", "يمكن رفض المواعيد المعلقة فقط");
                return "redirect:/Avocat/rdv/details/" + id;
            }

            // Refuser le rendez-vous
            rdv.setStatut(Statut.REFUSE);
            rdvService.save(rdv);

            redirectAttributes.addFlashAttribute("successMessage", "تم رفض الموعد بنجاح");
            return "redirect:/Avocat/rdv/details/" + id;
        } catch (Exception e) {
            logger.error("Erreur lors du refus du rendez-vous", e);
            redirectAttributes.addFlashAttribute("errorMessage", "حدث خطأ أثناء رفض الموعد: " + e.getMessage());
            return "redirect:/Avocat/rdv/list";
        }
    }

    @GetMapping("/rdv/disponibilites")
    @ResponseBody
    public List<String> getHeuresDisponibles(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        return rdvService.getHeuresDisponibles(avocat.getId(), date);
    }


    @GetMapping("/client/list")
    public String listClients(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        // Récupérer l'avocat connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Avocat avocat = (Avocat) auth.getPrincipal();

        // Pagination des clients
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<Client> clientPage = clientService.findByAvocat(avocat, pageable);

        // Si la page demandée n'existe pas, revenir à la première page
        if (page > 0 && page >= clientPage.getTotalPages()) {
            return "redirect:/Avocat/client/list?page=0";
        }

        model.addAttribute("clientPage", clientPage);
        model.addAttribute("clients", clientPage.getContent());
        model.addAttribute("currentPage", clientPage.getNumber() + 1);
        model.addAttribute("totalPages", clientPage.getTotalPages());
        model.addAttribute("avocat", avocat);

        return "Avocat/client/list";
    }
}