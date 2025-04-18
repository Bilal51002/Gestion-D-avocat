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
import java.util.List;

@Controller
@RequestMapping("/rdv")
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
        return "rdv/list";
    }

    // Afficher le formulaire de création
    @Transactional
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("rdv", new RDV());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("bureaux", bureauAvocatService.findAll());
        return "rdv/create";
    }

    // Traiter la création d'un RDV
    @PostMapping("/create")
    public String createRdv(@ModelAttribute("rdv") @Valid RDV rdv, BindingResult result, @RequestParam("client") Long clientId, @RequestParam("bureau") Long bureauId) {
        if (result.hasErrors()) {
            return "rdv/create";
        }
        rdv.setClient(clientService.findById(clientId));
        rdv.setBureau(bureauAvocatService.findById(bureauId));
        rdvService.save(rdv);
        return "redirect:/rdv/list";
    }
    // Afficher les détails d'un RDV
    @GetMapping("/details/{id}")
    public String showRdvDetails(@PathVariable("id") Long id, Model model) {
        RDV rdv = rdvService.findById(id);
        model.addAttribute("rdv", rdv);
        return "rdv/details";
    }

    // Afficher le formulaire de modification
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        RDV rdv = rdvService.findById(id);
        model.addAttribute("rdv", rdv);
        return "rdv/edit";
    }

    // Traiter la modification d'un RDV
    @PostMapping("/edit/{id}")
    public String updateRdv(@PathVariable("id") Long id, @Valid RDV rdv, BindingResult result) {
        if (result.hasErrors()) {
            return "rdv/edit";
        }
        rdv.setId(id);
        rdvService.save(rdv);
        return "redirect:/rdv/list";
    }

    // Supprimer un RDV
    @GetMapping("/delete/{id}")
    public String deleteRdv(@PathVariable("id") Long id) {
        RDV rdv = rdvService.findById(id);
        rdvService.delete(rdv);
        return "redirect:/rdv/list";
    }
}