package org.baeldung.web.controller;

import org.baeldung.persistence.dao.pfe.BureauAvocatRepository;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.RDV;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.baeldung.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/Secretaire/rdv")
public class RdvController {

    @Autowired
    private RdvService rdvService;
    @Autowired
    private ClientService clientService;

    @Autowired
    private BureauAvocatService bureauAvocatService;

    @Autowired
    private BureauAvocatRepository bureauAvocatRepository;
    private static final Logger logger = LoggerFactory.getLogger(RdvController.class);

    @GetMapping("/today")
    public String todayRdv(Model model) {
        return "redirect:/Secretaire/rdv/list?dateFilter=today";
    }

    @GetMapping("/list")
    public String listRdv(Model model,
                          @RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "size", defaultValue = "10") int size,
                          @RequestParam(name = "dateFilter", required = false) String dateFilter) {

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

        // Récupérer les RDV du bureau en fonction du filtre de date
        List<RDV> allRdvs;

        if ("today".equals(dateFilter)) {
            // Filtrer pour afficher uniquement les rendez-vous d'aujourd'hui
            LocalDate today = LocalDate.now();
            allRdvs = rdvService.findByBureauAvocat(bureau).stream()
                    .filter(rdv -> rdv.getDate() != null && rdv.getDate().equals(today))
                    .collect(Collectors.toList());

            model.addAttribute("filterTitle", "مواعيد اليوم"); // Titre en arabe pour "Rendez-vous d'aujourd'hui"
        } else {
            // Afficher tous les rendez-vous
            allRdvs = rdvService.findByBureauAvocat(bureau);
            model.addAttribute("filterTitle", "جميع المواعيد"); // Titre en arabe pour "Tous les rendez-vous"
        }

        // Pagination manuelle
        int start = page * size;
        int end = Math.min(start + size, allRdvs.size());
        List<RDV> paginatedRdvs = start < end ? allRdvs.subList(start, end) : Collections.emptyList();

        // Créer un objet de pagination simplifié
        Map<String, Object> rdvPage = new HashMap<>();
        rdvPage.put("content", paginatedRdvs);
        rdvPage.put("number", page);
        rdvPage.put("size", size);
        rdvPage.put("totalElements", allRdvs.size());
        rdvPage.put("totalPages", (int) Math.ceil((double) allRdvs.size() / size));
        rdvPage.put("first", page == 0);
        rdvPage.put("last", (page + 1) * size >= allRdvs.size());
        rdvPage.put("numberOfElements", paginatedRdvs.size());
        rdvPage.put("empty", paginatedRdvs.isEmpty());

        model.addAttribute("rdvPage", rdvPage);
        model.addAttribute("activeFilter", dateFilter); // Pour l'UI
        model.addAttribute("secretaire", secretaire);
        model.addAttribute("bureau", bureau);

        return "Secretaire/rdv/list";
    }
    @GetMapping("/available-dates")
    @ResponseBody
    public List<String> getAvailableDates(
            @RequestParam("bureauId") Long bureauId,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // Générer toutes les dates de la période
        List<String> availableDates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            // Exclure les weekends (samedi et dimanche)
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {

                // Vérifier si la date est disponible
                if (rdvService.isDateAvailable(bureauId, currentDate)) {
                    availableDates.add(currentDate.toString());
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return availableDates;
    }

    @GetMapping("/available-times")
@ResponseBody
public List<String> getAvailableTimes(
        @RequestParam("bureauId") Long bureauId,
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

    // Liste de tous les créneaux possibles (09:00 à 17:00)
    List<String> allTimeSlots = Arrays.asList(
            "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00"
    );

    // Récupérer tous les RDV pour ce bureau à cette date
    List<RDV> rdvs = rdvService.findByBureauIdAndDate(bureauId, date);

    // Si un RDV existe déjà ce jour-là, retourner une liste vide
    if (!rdvs.isEmpty()) {
        return new ArrayList<>();
    }

    return allTimeSlots;
}



    // Afficher le formulaire de création
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        Secretaire secretaire = (Secretaire) principal;
        String email = secretaire.getEmail();
        Long id = secretaire.getId();
        model.addAttribute("rdv", new RDV());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("bureaux", bureauAvocatRepository.findBysecretaire_id(id));
        return "Secretaire/rdv/create";
    }

    // Traiter la création d'un RDV
    @PostMapping("/create")
    public String createRdv(@ModelAttribute("rdv") @Valid RDV rdv,
                            BindingResult result,
                            @RequestParam("client") Long clientId,
                            @RequestParam("bureau") Long bureauId) {
        if (result.hasErrors()) {
            return "Secretaire/rdv/create";
        }
        rdvService.addRdv(rdv, clientId, bureauId);
        return "redirect:/Secretaire/rdv/list";
    }

    // Afficher les détails d'un RDV
    @GetMapping("/details/{id}")
    public String showRdvDetails(@PathVariable("id") Long id, Model model) {
        RDV rdv = rdvService.findById(id);
        model.addAttribute("rdv", rdv);
        return "Secretaire/rdv/details";
    }

    // Afficher le formulaire de modification
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        RDV rdv = rdvService.findById(id);
        model.addAttribute("rdv", rdv);
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("bureaux", bureauAvocatService.findAll());
        return "Secretaire/rdv/edit";
    }

    // Traiter la modification d'un RDV
    @PostMapping("/edit/{id}")
    public String updateRdv(@PathVariable("id") Long id,
                            @Valid RDV rdv,
                            BindingResult result,
                            @RequestParam("client") Long clientId,
                            @RequestParam("bureau") Long bureauId) {
        if (result.hasErrors()) {
            return "Secretaire/rdv/edit";
        }
        rdv.setId(id);
        rdvService.addRdv(rdv, clientId, bureauId);
        return "redirect:/Secretaire/rdv/list";
    }

    // Supprimer un RDV
    @GetMapping("/delete/{id}")
    public String deleteRdv(@PathVariable("id") Long id) {
        rdvService.deleteById(id);
        return "redirect:/Secretaire/rdv/list";
    }

    // Version simplifiée pour un ajout rapide via formulaire simple
    @PostMapping("/add")
    public String addRDV(@RequestParam("clientId") Long clientId,
                         @RequestParam("bureauId") Long bureauId,
                         @RequestParam("heur") String heur,
                         @RequestParam("sujet") String sujet,
                         @RequestParam("date") LocalDate date) {

        RDV rdv = new RDV();
        rdv.setHeur(heur);
        rdv.setSujet(sujet);
        rdv.setDate(date);
        rdvService.addRdv(rdv, clientId, bureauId);

        return "redirect:/Secretaire/rdv/list";
    }
}
