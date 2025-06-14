
package org.baeldung.web.controller;

import java.sql.Date;
import org.baeldung.persistence.dao.pfe.RdvRepository;
import org.baeldung.persistence.model.pfe.*;
import org.baeldung.persistence.model.pfe.RDV;
import org.baeldung.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.Base64.Encoder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.baeldung.persistence.dao.pfe.BureauAvocatRepository;
import org.baeldung.persistence.dao.pfe.TypeDossierRepository;
import org.baeldung.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientController {
	@Autowired
	BureauAvocatService bureauAvocatService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private  NotificationService notificationService;
	@Autowired
	private RdvService rdvService;
	@Autowired
	private BureauAvocatRepository bureauAvocatRepository;
	@Autowired
	AvocatService avocatService;
	@Autowired
	private RdvRepository rdvRepository;
	@Autowired
	UserService userService;
	@Autowired
	DossierService dossierService;
	@Autowired
	TypeDossierService typeDossierService;
	@Autowired
	SecretaireService secretaireService;
	@Autowired
	TypeDossierRepository typeDossierRepository;

	@ModelAttribute
	public void addGlobalAttributes(Model model) {
		// Get the current user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof Client) {
			Client client = (Client) auth.getPrincipal();

			// Add unread notifications count
			long unreadCount = notificationService.countNonLues(client);
			model.addAttribute("unreadNotificationsCount", unreadCount);
		}
	}
@GetMapping(value = "Client/Dashboard")
public String Dashboard(Model model, @RequestParam(defaultValue = "0") int page,
						@RequestParam(defaultValue = "5") int size) {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	Client client = (Client) auth.getPrincipal();

	// Charger le prochain RDV du client
	RDV prochainRdv = rdvService.findNextByClient(client.getId());

	// Récupérer tous les rendez-vous du client
	List<RDV> rdvList = rdvService.findAllByClient(client.getId());

	// Récupérer les dossiers du client
	List<Dossier> dossiers = dossierService.findDossiersByClientId(client.getId());

	// Récupérer les notifications du client
	List<Notification> notifications = notificationService.getNotificationsByUser(client);

	// Pagination des rendez-vous
	Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
	Page<RDV> rdvPage = rdvService.findByClientId(client.getId(), pageable);

	// Ajouter la liste des numéros de page pour la pagination
	if (rdvPage.getTotalPages() > 0) {
		List<Integer> pageNumbers = IntStream.rangeClosed(1, rdvPage.getTotalPages())
				.boxed()
				.collect(Collectors.toList());
		model.addAttribute("pageNumbers", pageNumbers);
	}

	model.addAttribute("client", client);
	model.addAttribute("rdvPage", rdvPage);
	model.addAttribute("avocats", avocatService.findAll());
	model.addAttribute("prochainRdv", prochainRdv);
	model.addAttribute("bureau", bureauAvocatService.findAll());
	model.addAttribute("rdvList", rdvList);
	model.addAttribute("dossiers", dossiers);
	model.addAttribute("notifications", notifications);

	return "Client/Dashboard";
}


	@GetMapping("/Client/notifications")
	public String listerNotifications(Model model) {
		// Récupérer l'utilisateur connecté
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Client client = (Client) auth.getPrincipal();

		// Ajouter les notifications au modèle
		model.addAttribute("notifications", notificationService.getNotificationsByUser(client));
		return "Client/notifications";
	}

	@GetMapping("/Client/notifications/{id}")
	public String afficherNotification(@PathVariable Long id, Model model) {
		Notification notification = notificationService.getNotificationById(id);
		if (notification != null) {
			notificationService.marquerCommeLue(id);
			model.addAttribute("notification", notification);

			// Si c'est une notification de rendez-vous, on ajoute le RDV au modèle
			if (notification.getType().startsWith("RDV_")) {
				model.addAttribute("rdv", notification.getRendezVous());
			}

			return "Client/notification-details";
		}
		return "redirect:/Client/Dashboard";
	}


	@GetMapping(value = "Client/dossier/details/{id}")
	public String dossierDetails(@PathVariable("id") Long id, Model model) {
		// Récupérer l'utilisateur connecté
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Client client = (Client) auth.getPrincipal();

		// Récupérer le dossier
		Dossier dossier = dossierService.getDossierById(id);

		// Vérifier que le dossier existe et appartient au client connecté
		boolean clientHasDossier = false;
		if (dossier != null && dossier.getClient() != null) {
			for (Client c : dossier.getClient()) {
				if (c.getId().equals(client.getId())) {
					clientHasDossier = true;
					break;
				}
			}
		}

		if (!clientHasDossier) {
			// Rediriger si le dossier n'existe pas ou n'appartient pas au client
			return "redirect:/Client/Dashboard";
		}

		model.addAttribute("client", client);
		model.addAttribute("dossier", dossier);

		return "Client/dossier/details";
	}

	// Méthode pour afficher/imprimer un rendez-vous
	@GetMapping("/Client/rdv/print/{id}")
	public String printRendezVous(@PathVariable("id") Long id, Model model) {
		// Récupérer le rendez-vous par son ID
		RDV rdv = rdvService.findById(id);

		// Vérifier si le rendez-vous existe
		if (rdv == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rendez-vous introuvable avec l'ID: " + id);
		}

		// Ajouter le rendez-vous au modèle
		model.addAttribute("rdv", rdv);

		// Retourner la vue d'impression
		return "Client/rdv/print";
	}
	@PostMapping("/rdv/demande")
	public String demanderRendezVous(@ModelAttribute RDV rendezVous,
									 @AuthenticationPrincipal Client client) {
		// Associer le client connecté
		rendezVous.setClient(client);

		// Enregistrer le rendez-vous (ceci déclenchera la notification)
		rdvService.creerRendezVous(rendezVous);

		return "redirect:/client/rdv/confirmation";
	}

	@GetMapping(value = "Client/rdv/heures-disponibles")
	@ResponseBody
	public List<String> getHeuresDisponibles(@RequestParam("avocatId") Long avocatId,
											 @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return rdvService.getHeuresDisponibles(avocatId, date);
	}

	@GetMapping(value = "Client/rdv/disponibilites")
	@ResponseBody
	public List<Map<String, Object>> getDisponibilites(
			@RequestParam("avocatId") Long avocatId,
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

		List<Map<String, Object>> events = new ArrayList<>();

		// Pour chaque jour dans la plage demandée
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			// Ne pas inclure les week-ends
			if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
				// Récupérer les heures disponibles pour ce jour
				List<String> heuresDisponibles = rdvService.getHeuresDisponibles(avocatId, date);

				// Créer un événement pour chaque heure disponible
				for (String heure : heuresDisponibles) {
					Map<String, Object> event = new HashMap<>();

					// Extraire l'heure et les minutes
					String[] parts = heure.split(":");
					int hour = Integer.parseInt(parts[0]);
					int minute = Integer.parseInt(parts[1]);

					// Créer la date/heure de début
					LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

					// Créer la date/heure de fin (30 minutes après)
					LocalDateTime endDateTime = startDateTime.plusMinutes(30);

					// Formater les dates pour FullCalendar
					event.put("title", "متاح");
					event.put("start", startDateTime.toString());
					event.put("end", endDateTime.toString());
					event.put("backgroundColor", "#4CAF50");
					event.put("textColor", "#ffffff");

					// Utiliser HashMap au lieu de Map.of pour éviter l'erreur
					Map<String, Object> extendedProps = new HashMap<>();
					extendedProps.put("heure", heure);
					event.put("extendedProps", extendedProps);

					events.add(event);
				}
			}
		}

		return events;
	}

	@GetMapping(value = "Client/rdv/create")
	public String showCreateRdvForm(Model model) {
		List<Avocat> avocats = avocatService.findAll();

		// Récupérer l'utilisateur connecté
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Client client = (Client) auth.getPrincipal();

		// Créer un nouveau RDV
		RDV rdv = new RDV();

		// Vérifier si c'est la première visite du client
		boolean estPremiereFois = rdvService.estPremiereFois(client.getId());
		rdv.setPremiereFois(estPremiereFois);

		// Add necessary objects to the model
		model.addAttribute("rdv", rdv);
		model.addAttribute("bureaux", bureauAvocatService.findAll());
		model.addAttribute("avocats", avocats);
		model.addAttribute("client", client);
		model.addAttribute("estPremiereFois", estPremiereFois);

		return "Client/rdv/create";
	}

	@GetMapping(value = "Client/rdv/heures-disponibles-avocat")
	@ResponseBody
	public List<String> getHeuresDisponiblesAvocat(
			@RequestParam("avocatId") Long avocatId,
			@RequestParam("bureauId") Long bureauId,
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		// Vérifier si l'avocat appartient au bureau
		boolean avocatAppartientAuBureau = avocatService.verifierAppartenanceBureau(avocatId, bureauId);

		if (!avocatAppartientAuBureau) {
			return Collections.emptyList(); // Retourner une liste vide si l'avocat n'appartient pas au bureau
		}

		return rdvService.getHeuresDisponibles(avocatId, date);
	}

	@GetMapping(value = "Client/rdv/heures-disponibles-bureau")
	@ResponseBody
	public List<String> getHeuresDisponiblesBureau(
			@RequestParam("bureauId") Long bureauId,
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		return rdvService.getHeuresDisponiblesBureau(bureauId, date);
	}

	@PostMapping(value = "Client/rdv/create")
	public String createRdv(@Valid @ModelAttribute("rdv") RDV rdv, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("bureaux", bureauAvocatService.findAll());
			model.addAttribute("avocats", avocatService.findAll());
			return "Client/rdv/create";
		}

		// Vérifier que l'heure est bien disponible (double vérification)
		List<String> heuresDisponibles = new ArrayList<>();

		Boolean estPremiereFois = rdv.isPremiereFois();
		if (estPremiereFois != null && estPremiereFois) {
			// Première visite - vérifier les heures disponibles pour le bureau
			if (rdv.getBureau() != null) {
				heuresDisponibles = rdvService.getHeuresDisponiblesBureau(rdv.getBureau().getId(), rdv.getDate());
			} else {
				result.rejectValue("bureau", "error.rdv", "يرجى اختيار المكتب");
				model.addAttribute("bureaux", bureauAvocatService.findAll());
				model.addAttribute("avocats", avocatService.findAll());
				return "Client/rdv/create";
			}
		} else {

			// Visite régulière - vérifier les heures disponibles pour l'avocat
			if (rdv.getAvocat() != null && rdv.getBureau() != null) {
				heuresDisponibles = rdvService.getHeuresDisponibles(rdv.getAvocat().getId(), rdv.getDate());
			} else {
				if (rdv.getAvocat() == null) {
					result.rejectValue("avocat", "error.rdv", "يرجى اختيار المحامي");
				}
				if (rdv.getBureau() == null) {
					result.rejectValue("bureau", "error.rdv", "يرجى اختيار المكتب");
				}
				model.addAttribute("bureaux", bureauAvocatService.findAll());
				model.addAttribute("avocats", avocatService.findAll());
				return "Client/rdv/create";
			}
		}

		if (!heuresDisponibles.contains(rdv.getHeur())) {
			result.rejectValue("heur", "error.rdv", "هذا الموعد لم يعد متاحًا");
			model.addAttribute("bureaux", bureauAvocatService.findAll());
			model.addAttribute("avocats", avocatService.findAll());
			return "Client/rdv/create";
		}

		// Set the current client as the client for this appointment
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Client client = (Client) auth.getPrincipal();
		rdv.setClient(client);

		try {
			// Utiliser la méthode du service qui crée le RDV et la notification
			RDV savedRdv = rdvService.creerRendezVous(rdv);
			return "redirect:/Client/rdv/details/" + savedRdv.getId();
		} catch (IllegalStateException e) {
			// Gestion des erreurs retournées par le service
			result.rejectValue("", "error.rdv", e.getMessage());
			model.addAttribute("bureaux", bureauAvocatService.findAll());
			model.addAttribute("avocats", avocatService.findAll());
			return "Client/rdv/create";
		}
	}

	@GetMapping(value = "Client/rdv/calendar")
	public String showCalendarView(Model model) {
		// Récupérer l'utilisateur connecté
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Client client = (Client) auth.getPrincipal();

		model.addAttribute("client", client);
		model.addAttribute("bureaux", bureauAvocatService.findAll());
		model.addAttribute("avocats", avocatService.findAll());
		model.addAttribute("estPremiereFois", rdvService.estPremiereFois(client.getId()));

		return "Client/rdv/calendar";
	}

	@GetMapping(value = "Client/rdv/disponibilites-avocat")
	@ResponseBody
	public List<Map<String, Object>> getDisponibilitesAvocat(
			@RequestParam("avocatId") Long avocatId,
			@RequestParam("bureauId") Long bureauId,
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

		// Vérifier si l'avocat appartient au bureau
		boolean avocatAppartientAuBureau = avocatService.verifierAppartenanceBureau(avocatId, bureauId);
		if (!avocatAppartientAuBureau) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> events = new ArrayList<>();

		// Pour chaque jour dans la plage demandée
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			// Ne pas inclure les week-ends
			if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
				// Récupérer les heures disponibles pour ce jour
				List<String> heuresDisponibles = rdvService.getHeuresDisponibles(avocatId, date);

				// Créer un événement pour chaque heure disponible
				for (String heure : heuresDisponibles) {
					Map<String, Object> event = new HashMap<>();

					// Extraire l'heure et les minutes
					String[] parts = heure.split(":");
					int hour = Integer.parseInt(parts[0]);
					int minute = Integer.parseInt(parts[1]);

					// Créer la date/heure de début
					LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

					// Créer la date/heure de fin (30 minutes après)
					LocalDateTime endDateTime = startDateTime.plusMinutes(60);

					// Formater les dates pour FullCalendar
					event.put("title", "متاح");
					event.put("start", startDateTime.toString());
					event.put("end", endDateTime.toString());
					event.put("backgroundColor", "#4CAF50");
					event.put("textColor", "#ffffff");

					Map<String, Object> extendedProps = new HashMap<>();
					extendedProps.put("heure", heure);
					event.put("extendedProps", extendedProps);

					events.add(event);
				}
			}
		}

		return events;
	}

	@GetMapping(value = "Client/rdv/disponibilites-bureau")
	@ResponseBody
	public List<Map<String, Object>> getDisponibilitesBureau(
			@RequestParam("bureauId") Long bureauId,
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

		List<Map<String, Object>> events = new ArrayList<>();

		// Pour chaque jour dans la plage demandée
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			// Ne pas inclure les week-ends
			if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
				// Récupérer les heures disponibles pour ce jour
				List<String> heuresDisponibles = rdvService.getHeuresDisponiblesBureau(bureauId, date);

				// Créer un événement pour chaque heure disponible
				for (String heure : heuresDisponibles) {
					Map<String, Object> event = new HashMap<>();

					// Extraire l'heure et les minutes
					String[] parts = heure.split(":");
					int hour = Integer.parseInt(parts[0]);
					int minute = Integer.parseInt(parts[1]);

					// Créer la date/heure de début
					LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

					// Créer la date/heure de fin (60 minutes après pour les RDV bureau)
					LocalDateTime endDateTime = startDateTime.plusMinutes(60);

					// Formater les dates pour FullCalendar
					event.put("title", "متاح");
					event.put("start", startDateTime.toString());
					event.put("end", endDateTime.toString());
					event.put("backgroundColor", "#4CAF50");
					event.put("textColor", "#ffffff");

					Map<String, Object> extendedProps = new HashMap<>();
					extendedProps.put("heure", heure);
					event.put("extendedProps", extendedProps);

					events.add(event);
				}
			}
		}

		return events;
	}

	@GetMapping(value = "Client/rdv/existants")
	@ResponseBody
	public List<Map<String, Object>> getRdvExistants(
			@RequestParam(value = "avocatId", required = false) Long avocatId,
			@RequestParam("bureauId") Long bureauId,
			@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

		List<Map<String, Object>> events = new ArrayList<>();
		List<RDV> rdvs;

		// Récupérer les rendez-vous selon le contexte (avocat ou bureau)
		if (avocatId != null) {
			// Rendez-vous spécifiques à un avocat
			rdvs = rdvRepository.findByAvocatIdAndDateBetween(avocatId, start, end);
		} else {
			// Tous les rendez-vous du bureau
			rdvs = rdvRepository.findByBureauIdAndDateBetween(bureauId, start, end);
		}

		for (RDV rdv : rdvs) {
			Map<String, Object> event = new HashMap<>();

			// Extraire l'heure et les minutes
			String[] parts = rdv.getHeur().split(":");
			int hour = Integer.parseInt(parts[0]);
			int minute = Integer.parseInt(parts[1]);

			// Créer la date/heure de début
			LocalDateTime startDateTime = LocalDateTime.of(rdv.getDate(), LocalTime.of(hour, minute));

			// Créer la date/heure de fin (durée: 30min pour avocat, 60min pour bureau)
			LocalDateTime endDateTime;
			if (rdv.getAvocat() != null) {
				endDateTime = startDateTime.plusMinutes(30);
			} else {
				endDateTime = startDateTime.plusMinutes(60);
			}

			// Formater les dates pour FullCalendar
			event.put("title", "محجوز");
			event.put("start", startDateTime.toString());
			event.put("end", endDateTime.toString());
			event.put("backgroundColor", "#F44336");
			event.put("textColor", "#ffffff");

			Map<String, Object> extendedProps = new HashMap<>();
			extendedProps.put("rdvId", rdv.getId());
			if (rdv.getAvocat() != null) {
				extendedProps.put("avocatId", rdv.getAvocat().getId());
				extendedProps.put("avocatName", rdv.getAvocat().getFirstName() + " " + rdv.getAvocat().getLastName());
			}
			extendedProps.put("bureauId", rdv.getBureau().getId());
			extendedProps.put("bureauName", rdv.getBureau().getNom());
			event.put("extendedProps", extendedProps);

			events.add(event);
		}

		return events;
	}
	//pour le modification de rdv
	@GetMapping(value = "Client/rdv/details/{id}")
	public String showRdvDetails(@PathVariable("id") Long id, Model model) {
		// Get the appointment
		RDV rdv = rdvService.findById(id);

		// Check if the appointment exists
		if (rdv == null) {
			return "redirect:/Client/rdv/list";
		}

		// Check if the current user is the owner of this appointment
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();
		Client client = (Client) principal;
		//Client client = clientService.findByEmail(user.getEmail());

		if (!rdv.getClient().getId().equals(client.getId())) {
			return "redirect:/Client/rdv/list";
		}

		model.addAttribute("rdv", rdv);
		return "Client/rdv/details";
	}
	// Afficher le formulaire de modification
	@GetMapping(value = "Client/rdv/edit/{id}")
	public String showEditRdvForm(@PathVariable("id") Long id, Model model) {
		// Récupérer le rendez-vous
		RDV rdv = rdvService.findById(id);

		// Vérifier si le rendez-vous existe
		if (rdv == null) {
			return "redirect:/Client/rdv/list";
		}

		// Vérifier si l'utilisateur actuel est le propriétaire de ce rendez-vous
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();
		Client client = (Client) principal;
		//Client client = clientService.findByEmail(user.getEmail());

		if (!rdv.getClient().getId().equals(client.getId())) {
			return "redirect:/Client/rdv/list";
		}

		// Ajouter les données nécessaires au modèle
		model.addAttribute("rdv", rdv);
		model.addAttribute("bureaux", bureauAvocatService.findAll());
		model.addAttribute("avocats", avocatService.findAll());

		return "Client/rdv/edit";
	}

	// Traiter la soumission du formulaire de modification
	@PostMapping(value = "Client/rdv/edit/{id}")
	public String updateRdv(@PathVariable("id") Long id, @Valid @ModelAttribute("rdv") RDV rdv,
							BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("bureaux", bureauAvocatService.findAll());
			model.addAttribute("avocats", avocatService.findAll());
			return "Client/rdv/edit";
		}

		// Récupérer le rendez-vous original
		RDV originalRdv = rdvService.findById(id);
		if (originalRdv == null) {
			return "redirect:/Client/rdv/list";
		}

		// Vérifier si l'utilisateur actuel est le propriétaire de ce rendez-vous
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();
		Client client = (Client) principal;
		//Client client = clientService.findByEmail(user.getEmail());

		if (!originalRdv.getClient().getId().equals(client.getId())) {
			return "redirect:/Client/rdv/list";
		}

		// Conserver le client original du rendez-vous
		rdv.setClient(client);
		rdv.setId(id); // S'assurer que l'ID est maintenu

		// Enregistrer les modifications
		rdvService.save(rdv);

		// Ajouter un message de succès
		redirectAttributes.addFlashAttribute("successMessage", "تم تعديل الموعد بنجاح");

		return "redirect:/Client/rdv/details/" + rdv.getId();
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Client/clients")
	public ModelAndView wxAutoLogin8(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "queryClient", defaultValue = "") String cl,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {
		System.err.println("clients");
		List<Client> clients = clientService.findByfirstName(cl);
		List<Client> client = clientService.findAll();
		List<Dossier> dossiers = dossierService.findAll();
		Client clientss = new Client();
		model.addAttribute("clients", clientss);
		model.addAttribute("clients", client);
		model.addAttribute("clients", clients);
		model.addAttribute("dossiers", dossiers);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Client/clients");

		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Client/clients", method = RequestMethod.POST)
	public String saveCleint(@RequestParam("file") MultipartFile file, @RequestParam("Cname") String firstName,
			@RequestParam("Clast") String LastName, @RequestParam("Cemail") String email,
			@RequestParam("Ctel") String tel, @RequestParam("CStelfixe") String telfixe,
			@RequestParam("Cadresse") String adresse, @RequestParam("CDate") Date DateCreation,
			@RequestParam("Cpassw") String password, @RequestParam("CCarte") String CarteNational) {

		System.err.println("ajoute Client");
		clientService.saveClienttoDB(file, firstName, LastName, email, tel, telfixe, adresse, DateCreation, password,
				CarteNational);
		return "redirect:/Admin/AdminAvocat/Client/clients";

	}

	@GetMapping(value = "/Admin/AdminAvocat/Client/rechercheClient")
	public ModelAndView wxAutoLogin80(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "queryClient", defaultValue = "") String cl,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {

		System.err.println("clients");
		List<Client> clients = clientService.findByfirstName(cl);
		List<Client> client = clientService.findAll();
		List<Dossier> dossiers = dossierService.findAll();
		model.addAttribute("clients", client);
		model.addAttribute("clients", clients);
		model.addAttribute("dossiers", dossiers);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Client/clients-list");

		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Client/clients-list")
	public ModelAndView wxAutoLogin12(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "queryClient", defaultValue = "") String cl,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {

		System.err.println("clients");
		List<Client> clients = clientService.findByfirstName(cl);
		List<Client> client = clientService.findAll();
		List<Dossier> dossiers = dossierService.findAll();
		model.addAttribute("clients", client);
		model.addAttribute("clients", clients);
		model.addAttribute("dossiers", dossiers);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Client/clients-list");

		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Client/client-profil")
	public ModelAndView wxAutoLogin9(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("client") Long client, @RequestParam(name = "query", defaultValue = "") String md)
			throws Exception {
		List<Dossier> dossier = dossierService.findAll();
		List<Dossier> dossiers = dossierService.findBynumeroDossier(md);
		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		Client result = clientService.findById(client);

		System.err.println(result.getId());

		model.addAttribute("client", result);
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossier);
		model.addAttribute("dossiers", dossiers);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Client/client-profil");

		return ret;
	}

	@RequestMapping(value = "/Admin/index")
	public ModelAndView wxAutoLogin1(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {

		System.err.println("index");
		List<org.baeldung.persistence.model.User> users=userService.findByfirstName(mv);
		List<BureauAvocat> bureaux = bureauAvocatService.findByNom(mc);
		List<Barreau> barreaux = bureauAvocatService.findAllBarreaux();
		List<Ville> villes = bureauAvocatService.findAllVilles();
		/* List<Avocat> avocats = bureauAvocatService.findByName(); */

		Encoder encoder = Base64.getEncoder();
		model.addAttribute("users", users);
		model.addAttribute("bureaux", bureaux);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("villes", villes);
		if (bureaux == null || bureaux.isEmpty()) {
			System.out.println("Aucun bureau trouvé !");
			// Vous pouvez définir un message par défaut ou autre traitement
		}
		ModelAndView ret = new ModelAndView("Admin/index");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");

		System.out.println("user :  admin :" + user.getEmail());
		System.out.println(user.getRoles());
		if (user != null) {
			ret.addObject("user", user);
		}
		return ret;

	}

	@RequestMapping(value = "/Client/index")
	public ModelAndView wxAutoLogin11(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {
		System.err.println("index");
		List<Avocat> avocat = avocatService.findByfirstName(mc);
		System.out.println(avocat.get(0).getFirstName());
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());

		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");

		System.out.println("usernnnnnnnnnnn:user MdhM :" + user.getEmail());
		System.out.println(user.getRoles());
		if (user != null) {
			ret.addObject("user", user);
		}
		return ret;

	}

	@RequestMapping(value = "/Admin/users")
	public ModelAndView wxAutoLogin10(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String ml,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn :testu :" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/users");
		return ret;
	}

	@GetMapping(value = "/Admin/rechercheUser")
	public ModelAndView wxAutoLogin100(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String ml,
			@RequestParam(name = "queryUser", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {
		System.err.println("users");
		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("avocats", avocats);
		model.addAttribute("avocats", avocat);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/users");
		return ret;
	}

	@RequestMapping(value = "/Avocat/index")
	public ModelAndView ModelAndViewwxAutoLogin2(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnnnnnnnnn :index :" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/index");
		return ret;

	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/AddFolder")
	public ModelAndView ModelAndViewwxAutoLogin5(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/AddFolder");
		return ret;

	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/search")
	public ModelAndView wxAutoLogin14(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "quer", defaultValue = "") String md) throws Exception {

		System.err.println("search");
		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/search");
		return ret;
	}

	@GetMapping(value = "Client/dossier/dossiers")
	public String listDossiers(Model model,
							   @RequestParam(defaultValue = "0") int page,
							   @RequestParam(defaultValue = "10") int size) {
		// Get the current authenticated client
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Client client = (Client) auth.getPrincipal();

		// Pagination for dossiers with sorting by creation date (newest first)
		Pageable pageable = PageRequest.of(page, size, Sort.by("DateCreation").descending());

		// Get the client's dossiers with pagination
		Page<Dossier> dossierPage = dossierService.findDossiersByClientIdPaginated(client.getId(), pageable);

		// Get all dossiers for the client (to check if empty)
		List<Dossier> dossiers = dossierService.findDossiersByClientId(client.getId());

		// Get all types of dossiers for filtering
		List<TypeDossier> typeDossiers = typeDossierService.findAll();

		// Add page numbers for pagination
		if (dossierPage.getTotalPages() > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, dossierPage.getTotalPages())
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		// Add attributes to the model
		model.addAttribute("client", client);
		model.addAttribute("dossiers", dossiers);
		model.addAttribute("dossierPage", dossierPage);
		model.addAttribute("typeDossiers", typeDossiers);
		model.addAttribute("bureau", bureauAvocatService.findAll());

		return "Client/dossier/dossiers";
	}
	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/dossier")
	public ModelAndView wxAutoLogin016(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "queryDossiers", defaultValue = "") String mds,
			@RequestParam(name = "que", defaultValue = "") String mn) throws Exception {

		System.err.println("dossier");
		List<Dossier> dossiers = dossierService.findBynumeroDossier(mds);
		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();

		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/dossier");
		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/dossier-list")
	public ModelAndView wxAutoLogin16(HttpServletRequest request, HttpServletResponse response, Model model,
									  @RequestParam(name = "query", defaultValue = "") String mc,
									  @RequestParam(name = "qury", defaultValue = "") String mv,
									  @RequestParam(name = "queryDossier", defaultValue = "") String md) throws Exception {

		System.err.println("dossier-list");

		List<Dossier> dossiers = dossierService.findBynumeroDossier(md);
		/* List<Dossier> dossiers1=dossierService.findBynumeroNational(mn); */
		/*
		 * List<Dossier> dossiers=dossierService.findBynumeroDossierOrnumeroNational(md,
		 * mn);
		 */

		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<TypeDossier> typeDossierss = typeDossierService.findAllTypeDossier();
		List<TypeDossier> typeDossiers = typeDossierService.findAll();

		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		/*
		 * System.out.println(encoder.encodeToStringavocats.get(0).getImguser().getBytes
		 * ()));
		 */
		/* model.addAttribute("secretaires",secretaires); */
		/* Dossier dossier = new Dossier(); */
		/* model.addAttribute("dossier", dossier); */
		model.addAttribute("typeDossiers", typeDossiers);
		model.addAttribute("typeDossiers", typeDossierss);
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		/* model.addAttribute("dossiers",dossiers1); */
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/dossier-list");
		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/addd", method = RequestMethod.POST)

	public String saveDossier(

			@RequestParam("Dnum") String numeroDossier,

			@RequestParam("Ddate") Date DateCreation,

			@RequestParam("DtypeCas") String typeDecas,

			@RequestParam("DtypeP") String typeProsedure,

			@RequestParam("DdateP") Date dateProchSession,

			@RequestParam("DnumN") String numeroNational,

			@RequestParam("Dtype") Long Dtype,

			@RequestParam("Dsujet") String sujet) {
		System.err.println("ajoute dossier");
		/*
		 * dossierService.saveDossiertoDB(numeroDossier, DateCreation,
		 * typeDecas,typeProsedure, dateProchSession, numeroNational, id, sujet);
		 */

		System.out.println(numeroDossier +" * "+DateCreation +" * "+typeDecas +" * "+typeProsedure +" * "+dateProchSession +" * "+Dtype +" * "+numeroNational +" * "+sujet);

		TypeDossier type = new TypeDossier();
		/* System.err.println(Dtype); */
		type = typeDossierRepository.findTypeDossierById(Dtype);
		System.err.println(type.getNom());

		BureauAvocat bureauAvocat = bureauAvocatRepository.findAll().get(0);
		Dossier d = new Dossier(numeroDossier, DateCreation, typeDecas, sujet, typeProsedure, dateProchSession, numeroNational, type,bureauAvocat);
		dossierService.addDossier(d);

		return "redirect:/Admin/AdminAvocat/Avocat/dossier-list";
	}

	@GetMapping(value = "/Admin/AdminAvocat/Avocat/cherchedossierc")
	public ModelAndView wxAutoLogin0116(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "queryDossiers", defaultValue = "") String mds,
			@RequestParam(name = "que", defaultValue = "") String mn) throws Exception {

		System.err.println("dossier");
		List<Dossier> dossiers = dossierService.findBynumeroDossier(mds);
		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();

		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/dossier");
		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/dossierdetils")
	public ModelAndView wxAutoLogin06(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "quer", defaultValue = "") String md,
			@RequestParam(name = "quer", defaultValue = "") String mn) throws Exception {

		System.err.println("dossier");

		List<Dossier> dossiers = dossierService.findBynumeroDossier(md);

		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();

		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/dossierdetils");
		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/profile-AdminAvo")
	public ModelAndView ModelAndViewwxAutoLogin6(Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "queryDossiers", defaultValue = "") String mds,
			@RequestParam(name = "que", defaultValue = "") String mn, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		System.err.println("dossier");
		List<Dossier> dossiers = dossierService.findBynumeroDossier(mds);

		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();

		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);

		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/profile-AdminAvo");
		return ret;

	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/profile-sA")
	public ModelAndView ModelAndViewwxAutoLogin7(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("avocat") Long avocat) throws Exception {

		Avocat result = avocatService.findById(avocat);

		System.err.println(result.getId());
		model.addAttribute("avocat", result);

		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}

		ret.setViewName("/Admin/AdminAvocat/Avocat/profile-sA");
		return ret;

	}

	@RequestMapping(value = "/Admin/AdminAvocat/Secretaire/secretaire-profil")
	public ModelAndView wxAutoLogin90(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "quer", defaultValue = "") String mds,
			@RequestParam(name = "quer", defaultValue = "") String mn, @RequestParam("secretaire") Long secretaire)
			throws Exception {
		Secretaire result = secretaireService.findById(secretaire);

		System.err.println(result.getId());
		model.addAttribute("secretaire", result);

		System.err.println("dossier");
		List<Dossier> dossiers = dossierService.findBynumeroDossier(mds);

		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();

		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);

		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Secretaire/secretaire-profil");

		return ret;
	}

	@GetMapping(value = "/Admin/AdminAvocat/Avocat/employees")
	public ModelAndView ModelAndViewwxAutoLogin31(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {

		System.err.println("employee");
		/* List<Client> client =avocatService.findAllClient(); */
		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		Avocat avocatAj = new Avocat();
		model.addAttribute("avocatAj", avocatAj);
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);

		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/employees");
		return ret;

	}

	@RequestMapping(value = "/Admin/AdminAvocat/Secretaire/secretaires-liste")
	public ModelAndView ModelAndViewwxAutoLogin83(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "queryAvocat", defaultValue = "") String mt,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {
		System.err.println("secretaire-list");
		List<Secretaire> secretaires = secretaireService.findByfirstName(mt);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		List<Secretaire> secretairess = secretaireService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		Avocat avocatAj = new Avocat();
		model.addAttribute("avocatAj", avocatAj);
		model.addAttribute("secretairess", secretairess);
		model.addAttribute("secretaires", secretaires);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Secretaire/secretaires-liste");
		return ret;

	}

	@GetMapping(value = "/Admin/AdminAvocat/Secretaire/chercheSecretaire")
	public ModelAndView ModelAndViewwxAutoLogin803(HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam(name = "querySecretaire", defaultValue = "") String mt,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {
		System.err.println("secretaire-list");
		List<Secretaire> secretaires = secretaireService.findByfirstName(mt);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		List<Secretaire> secretairess = secretaireService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		Avocat avocatAj = new Avocat();
		model.addAttribute("avocatAj", avocatAj);
		model.addAttribute("secretairess", secretairess);
		model.addAttribute("secretaires", secretaires);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Secretaire/secretaires-liste");
		return ret;

	}

	@GetMapping(value = "/Admin/AdminAvocat/Avocat/cherchedossier")
	public ModelAndView wxAutoLogin106(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "queryDossier", defaultValue = "") String md) throws Exception {

		System.err.println("dossier-list");
		List<Dossier> dossiers = dossierService.findBynumeroDossier(md);
		System.out.println("###" + dossiers.get(0).getTribunal().size());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		/* List<Secretaire> secretaires =secretaireService.findAll(); */

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		/* model.addAttribute("dossiers",dossiers1); */
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn :testt :" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/dossier-list");
		return ret;
	}
	  @RequestMapping(value = "/Admin/AdminAvocat/Avocat/deleteD/{id}")

	  public String deleteDossier(@PathVariable(name="id") Long id) {

	  System.err.println("delete Dossier"); dossierService.deleteDossier(id);
	  return "redirect:/Admin/AdminAvocat/Avocat/dossier-list"; }


	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/delete/{id}")

	public String deleteAvocat(@PathVariable(name = "id") Long id) {

		System.err.println("delete avocat");
		avocatService.deleteAvocat(id);
		return "redirect:/Admin/AdminAvocat/Avocat/employees";
	}

	@GetMapping(value = "/Admin/AdminAvocat/Secretaire/delete/{id}")

	public String deleteSecretaire(@PathVariable(name = "id") Long id) {

		System.err.println("delete secretaire");
		secretaireService.deleteSecretaire(id);
		return "redirect:/Admin/AdminAvocat/Secretaire/secretaires";
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Secretaire/secretaires")
	public ModelAndView wxAutoLogin0160(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "query", defaultValue = "") String mc,
			@RequestParam(name = "query", defaultValue = "") String mv,
			@RequestParam(name = "queryDossiers", defaultValue = "") String mds,
			@RequestParam(name = "que", defaultValue = "") String mn) throws Exception {
		Secretaire secretaire = new Secretaire();
		model.addAttribute("secretaire", secretaire);

		System.err.println("dossier");
		List<Dossier> dossiers = dossierService.findBynumeroDossier(mds);

		System.out.println("###" + dossiers.get(0).getTribunal());
		List<Tribunal> tribunals = dossierService.findAllnom();

		List<Avocat> avocat = avocatService.findByfirstName(mc);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();

		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();
		List<Secretaire> secretaires = secretaireService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("secretaires", secretaires);
		model.addAttribute("tribunals", tribunals);
		model.addAttribute("dossiers", dossiers);
		model.addAttribute("avocats", avocats);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnn S:" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Secretaire/secretaires");
		return ret;
	}

	@RequestMapping(value = "/Admin/AdminAvocat/Secretaire/secretaires", method = RequestMethod.POST)
	public String saveSecretaire(@RequestParam("file") MultipartFile file, @RequestParam("Sname") String firstName,
			@RequestParam("Slast") String LastName, @RequestParam("Semail") String email,
			@RequestParam("Stel") String tel, @RequestParam("Stelfixe") String telfixe,
			@RequestParam("Sadresse") String adresse, @RequestParam("SDate") Date DateCreation,
			@RequestParam("Spassw") String password, @RequestParam("SCarte") String CarteNational) {

		System.err.println("ajoute secretaier");
		secretaireService.saveSecretairetoDB(file, firstName, LastName, email, tel, telfixe, adresse, DateCreation,
				password, CarteNational);

		return "redirect:/Admin/AdminAvocat/Secretaire/secretaires";

	}

	@RequestMapping(value = "/Admin/AdminAvocat/Secretaire/secretaires-liste", method = RequestMethod.POST)
	public String saveSecretaire1(@RequestParam("file") MultipartFile file, @RequestParam("Sname") String firstName,
			@RequestParam("Slast") String LastName, @RequestParam("Semail") String email,
			@RequestParam("Stel") String tel, @RequestParam("Stelfixe") String telfixe,
			@RequestParam("Sadresse") String adresse, @RequestParam("SDate") Date DateCreation,
			@RequestParam("Spassw") String password, @RequestParam("SCarte") String CarteNational) {

		System.err.println("ajoute secretaier");
		secretaireService.saveSecretairetoDB(file, firstName, LastName, email, tel, telfixe, adresse, DateCreation,
				password, CarteNational);

		return "redirect:/Admin/AdminAvocat/Secretaire/secretaires-liste";

	}

	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/employees-list")
	public ModelAndView ModelAndViewwxAutoLogin8(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "queryAvocat", defaultValue = "") String mt,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {
		System.err.println("employee-list");
		List<Avocat> avocat = avocatService.findByfirstName(mt);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		Avocat avocatAj = new Avocat();
		model.addAttribute("avocatAj", avocatAj);
		model.addAttribute("avocats", avocat);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnn :" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/employees-list");
		return ret;

	}

	@GetMapping(value = "/Admin/AdminAvocat/Avocat/chercheAvocat")
	public ModelAndView ModelAndViewwxAutoLogin81(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(name = "queryAvocat", defaultValue = "") String mt,
			@RequestParam(name = "query", defaultValue = "") String mv) throws Exception {
		Avocat avocatss = new Avocat();
		model.addAttribute("Avocats", avocatss);
		System.err.println("employee-list");

		List<Avocat> avocat = avocatService.findByfirstName(mt);
		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);

		List<Barreau> barreaux = avocatService.findAllBarreaux();
		List<Ville> villes = avocatService.findAllVilles();
		List<Avocat> avocats = avocatService.findAll();

		Encoder encoder = Base64.getEncoder();
		System.out.println(avocats.size());
		model.addAttribute("avocats", avocat);
		model.addAttribute("barreaux", barreaux);
		model.addAttribute("barreaux1", barreaux1);
		model.addAttribute("villes", villes);
		ModelAndView ret = new ModelAndView();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user1");
		System.out.println("usernnnnnnnnnnnnnnnnnnn" + user.getEmail());
		if (user != null) {
			ret.addObject("user", user);
		}
		ret.setViewName("/Admin/AdminAvocat/Avocat/employees-list");
		return ret;

	}


	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/employees-list", method = RequestMethod.POST)

	public String saveAvocat(@RequestParam("file") MultipartFile file, @RequestParam("Aname") String firstName,
			@RequestParam("Alast") String LastName, @RequestParam("Aemail") String email,
			@RequestParam("Atel") String tel, @RequestParam("Atelfixe") String telfixe,
			@RequestParam("Aadresse") String adresse, @RequestParam("ADate") Date DateCreation,
			@RequestParam("Apassw") String password, @RequestParam("ABarreau") Barreau idBarreau,
			@RequestParam("ACarte") String CarteNational) {
		System.err.println("ajoute Avocat");
		avocatService.saveAvocattoDB(file, firstName, LastName, email, tel, telfixe, adresse, DateCreation, password,
				idBarreau, CarteNational);
		return "redirect:/Admin/AdminAvocat/Avocat/employees-list";
	}
	@GetMapping("/index*")
	public String index(Model model) {
		System.out.println("test1");
		return "Client/index";
	}


}
