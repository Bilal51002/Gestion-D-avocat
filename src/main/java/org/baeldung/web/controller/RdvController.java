package org.baeldung.web.controller;

import org.baeldung.persistence.model.pfe.RDV;
import org.baeldung.service.BureauAvocatService;
import org.baeldung.service.ClientService;
import org.baeldung.service.RdvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/Secretaire/rdv")
public class RdvController {

    @Autowired
    private RdvService rdvService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BureauAvocatService bureauAvocatService;

    // Afficher tous les RDV
    @GetMapping("/list")
    public String listRdv(Model model) {
        List<RDV> rdvs = rdvService.findAll();
        model.addAttribute("rdvs", rdvs);
        return "Secretaire/rdv/list";
    }

    // Afficher le formulaire de création
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("rdv", new RDV());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("bureaux", bureauAvocatService.findAll());
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
