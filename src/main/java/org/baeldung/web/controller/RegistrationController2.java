package org.baeldung.web.controller;

import org.baeldung.persistence.model.pfe.*;
import org.baeldung.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Base64;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController2 {
    @Autowired
    public SecretaireService secretaireService;
    @Autowired
    public DossierService dossierService;
    @Autowired
    public AvocatService avocatService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private BureauAvocatService bureauAvocatService;

    @RequestMapping(value = "/Secretaire/secretaires")
    public ModelAndView showSecretaires(HttpServletRequest request, HttpServletResponse response, Model model,
                                        @RequestParam(name = "query", defaultValue = "") String mc,
                                        @RequestParam(name = "query2", defaultValue = "") String mv,
                                        @RequestParam(name = "queryDossiers", defaultValue = "") String mds,
                                        @RequestParam(name = "que", defaultValue = "") String mn) throws Exception {

        Secretaire secretaire = new Secretaire();
        model.addAttribute("secretaire", secretaire);

        ModelAndView ret = new ModelAndView();
        try {
            List<Secretaire> secretaires = secretaireService.findAll();
            model.addAttribute("secretaires", secretaires);

            // Ces listes sont conservées car elles pourraient être nécessaires pour les formulaires
            // ou filtres, mais on pourrait les supprimer si non utilisées
            if (!mds.isEmpty()) {
                List<Dossier> dossiers = dossierService.findBynumeroDossier(mds);
                if (dossiers != null && !dossiers.isEmpty()) {
                    model.addAttribute("dossiers", dossiers);
                }
            }

            List<Tribunal> tribunals = dossierService.findAllnom();
            List<Barreau> barreaux = avocatService.findAllBarreaux();
            List<Ville> villes = avocatService.findAllVilles();

            if (!mc.isEmpty()) {
                List<Avocat> avocat = avocatService.findByfirstName(mc);
                model.addAttribute("avocat", avocat);
            }

            if (!mv.isEmpty()) {
                List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
                model.addAttribute("barreaux1", barreaux1);
            }

            model.addAttribute("tribunals", tribunals);
            model.addAttribute("barreaux", barreaux);
            model.addAttribute("villes", villes);

        } catch (Exception e) {
            // Journalisation de l'erreur (à mettre en place avec un logger)
            System.err.println("Erreur lors de l'affichage des secrétaires: " + e.getMessage());
            model.addAttribute("errorMessage", "Une erreur est survenue lors du chargement des données.");
        }

        ret.setViewName("/Secretaire/secretaires");
        return ret;
    }

    @RequestMapping(value = "/Secretaire/secretaires/add", method = RequestMethod.POST)
    public String saveSecretairePublic(@RequestParam("file") MultipartFile file,
                                       @RequestParam("Sname") String firstName,
                                       @RequestParam("Slast") String LastName,
                                       @RequestParam("Semail") String email,
                                       @RequestParam("Stel") String tel,
                                       @RequestParam("Stelfixe") String telfixe,
                                       @RequestParam("Sadresse") String adresse,
                                       @RequestParam("SDate") Date DateCreation,
                                       @RequestParam("Spassw") String password,
                                       @RequestParam("SCarte") String CarteNational,
                                       RedirectAttributes redirectAttributes) {

        try {
            // Validation des données d'entrée
            if (firstName.isEmpty() || LastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Veuillez remplir tous les champs obligatoires.");
                return "redirect:/Secretaire/secretaires";
            }

            // Vérification si l'email existe déjà
//        if (secretaireService.findByEmail(email)) {
//            redirectAttributes.addFlashAttribute("errorMessage",
//                "Un compte avec cet email existe déjà.");
//            return "redirect:/public/secretaires";
//        }

            // Enregistrement du secrétaire
            secretaireService.saveSecretairetoDB(file, firstName, LastName, email, tel, telfixe,
                    adresse, DateCreation, password, CarteNational);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Le secrétaire a été ajouté avec succès!");

        } catch (Exception e) {
            // Journalisation de l'erreur (à mettre en place avec un logger)
            System.err.println("Erreur lors de l'ajout du secrétaire: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Une erreur est survenue lors de l'enregistrement. Veuillez réessayer.");
        }

        return "redirect:/Secretaire/secretaires";
    }

    @RequestMapping(value = "/Client/clients")
    public ModelAndView showClients(HttpServletRequest request, HttpServletResponse response, Model model,
                                    @RequestParam(name = "query", defaultValue = "") String mc,
                                    @RequestParam(name = "query2", defaultValue = "") String mv,
                                    @RequestParam(name = "queryDossiers", defaultValue = "") String mds,
                                    @RequestParam(name = "que", defaultValue = "") String mn) throws Exception {

        Client client = new Client();
        model.addAttribute("client", client);

        ModelAndView ret = new ModelAndView();
        try {
            List<Client> clients = clientService.findAll();
            model.addAttribute("clients", clients);
            if (!mds.isEmpty()) {
                List<Dossier> dossiers = dossierService.findBynumeroDossier(mds);
                if (dossiers != null && !dossiers.isEmpty()) {
                    model.addAttribute("dossiers", dossiers);
                }
            }

            List<Tribunal> tribunals = dossierService.findAllnom();
            List<Barreau> barreaux = avocatService.findAllBarreaux();
            List<Ville> villes = avocatService.findAllVilles();

            if (!mc.isEmpty()) {
                List<Avocat> avocat = avocatService.findByfirstName(mc);
                model.addAttribute("avocat", avocat);
            }

            if (!mv.isEmpty()) {
                List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
                model.addAttribute("barreaux1", barreaux1);
            }

            model.addAttribute("tribunals", tribunals);
            model.addAttribute("barreaux", barreaux);
            model.addAttribute("villes", villes);

        } catch (Exception e) {
            // Journalisation de l'erreur (à mettre en place avec un logger)
            System.err.println("Erreur lors de l'affichage des clients: " + e.getMessage());
            model.addAttribute("errorMessage", "Une erreur est survenue lors du chargement des données.");
        }

        ret.setViewName("/Client/clients");
        return ret;
    }

    @RequestMapping(value = "/Client/clients/add", method = RequestMethod.POST)
    public String saveClient(@RequestParam("file") MultipartFile file,
                             @RequestParam("Cname") String firstName,
                             @RequestParam("Clast") String LastName,
                             @RequestParam("Cemail") String email,
                             @RequestParam("Ctel") String tel,
                             @RequestParam("Ctelfixe") String telfixe,
                             @RequestParam("Cadresse") String adresse,
                             @RequestParam("CDate") Date DateCreation,
                             @RequestParam("Cpassw") String password,
                             @RequestParam("CCarte") String CarteNational,
                             RedirectAttributes redirectAttributes) {

        try {
            // Validation des données d'entrée
            if (firstName.isEmpty() || LastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Veuillez remplir tous les champs obligatoires.");
                return "redirect:/Client/clients";
            }
            clientService.saveClienttoDB(file, firstName, LastName, email, tel, telfixe,
                    adresse, DateCreation, password, CarteNational);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Le clients a été ajouté avec succès!");

        } catch (Exception e) {
            // Journalisation de l'erreur (à mettre en place avec un logger)
            System.err.println("Erreur lors de l'ajout du client: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Une erreur est survenue lors de l'enregistrement. Veuillez réessayer.");
        }

        return "redirect:/Client/clients";
    }

    @RequestMapping(value = "/Avocat/employee")
    public ModelAndView showAvocats(HttpServletRequest request, HttpServletResponse response, Model model,
                                    @RequestParam(name = "queryAvocat", defaultValue = "") String mt,
                                    @RequestParam(name = "query", defaultValue = "") String mv) {
        ModelAndView ret = new ModelAndView("/Avocat/employee");

        List<Avocat> avocats = mt.isEmpty() ? avocatService.findAll() : avocatService.findByfirstName(mt);
        List<Barreau> barreaux1 = mv.isEmpty() ? null : avocatService.findBynomBarreau(mv);

        model.addAttribute("avocatAj", new Avocat());
        model.addAttribute("avocats", avocats);
        model.addAttribute("barreaux", avocatService.findAllBarreaux());
        model.addAttribute("barreaux1", barreaux1);
        model.addAttribute("villes", avocatService.findAllVilles());

        return ret;
    }

    @RequestMapping(value = "/Avocat/employee/add", method = RequestMethod.POST)
    public String saveAvocat(@RequestParam("file") MultipartFile file,
                             @RequestParam("Aname") String firstName,
                             @RequestParam("Alast") String LastName,
                             @RequestParam("Aemail") String email,
                             @RequestParam("Atel") String tel,
                             @RequestParam("Atelfixe") String telfixe,
                             @RequestParam("Aadresse") String adresse,
                             @RequestParam("ADate") Date dateCreation,
                             @RequestParam("Apassw") String password,
                             @RequestParam("ABarreau") Long idBarreau,
                             @RequestParam("ACarte") String carteNational) {
        Barreau barreau = avocatService.findBarreauById(idBarreau);
        avocatService.saveAvocattoDB(file, firstName, LastName, email, tel, telfixe,
                adresse, dateCreation, password, barreau, carteNational);
        return "redirect:/Avocat/employee";
    }

    @GetMapping("/bureau/liste")
    public ModelAndView showBureaux(
            @RequestParam(name = "query", defaultValue = "") String mc,
            Model model) {

        ModelAndView mav = new ModelAndView("/bureau/liste");

        // Récupérer les bureaux (filtrés ou tous)
        List<BureauAvocat> bureaux = mc.isEmpty() ?
                bureauAvocatService.findAll() :
                bureauAvocatService.findByNom(mc);

        // Ajouter les données au modèle
        model.addAttribute("bureauAj", new BureauAvocat());
        model.addAttribute("bureaux", bureaux);
        model.addAttribute("barreaux", bureauAvocatService.findAllBarreaux());
        model.addAttribute("villes", bureauAvocatService.findAllVilles());

        return mav;
    }

    @PostMapping("/bureau/ajouter")
    public String saveBureau(
            @RequestParam("image") MultipartFile file,
            @RequestParam("firstName") String nom,
            @RequestParam("email") String email,
            @RequestParam("mobilePhone") String tel,
            @RequestParam("fixedPhone") String telfex,
            @RequestParam("adresse") String adresse,
            @RequestParam("password") String password) {

        try {
            // Créer un nouvel objet BureauAvocat
            BureauAvocat bureau = new BureauAvocat();
            bureau.setNom(nom);
            bureau.setEmail(email);
            bureau.setTel(tel);
            bureau.setTelfex(telfex);
            bureau.setAdresse(adresse);
            bureau.setPassword(password);
            bureau.setSupp(0); // Par défaut, non supprimé

            // Gestion de l'image
            if (!file.isEmpty()) {
                String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
                bureau.setImage(base64Image);
            }

            // Enregistrer le bureau
            bureauAvocatService.addBureauAvocat(bureau);

            return "redirect:/bureau/liste";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/bureau/liste?error=true";
        }
    }

    @GetMapping("/bureau/nouveau")
    public String showAddBureauForm(Model model) {
        // Préparer le modèle pour le formulaire d'ajout
        model.addAttribute("bureauAj", new BureauAvocat());
        model.addAttribute("barreaux", bureauAvocatService.findAllBarreaux());
        model.addAttribute("villes", bureauAvocatService.findAllVilles());

        return "/bureau/ajout";
    }
}