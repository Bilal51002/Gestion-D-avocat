
package org.baeldung.web.controller;

import java.sql.Date;

import org.baeldung.persistence.dao.pfe.ClientRepository;
import org.springframework.ui.Model;
import java.util.Base64;
import java.util.List;
import java.util.Base64.Encoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.baeldung.persistence.dao.pfe.BureauAvocatRepository;
import org.baeldung.persistence.dao.pfe.TypeDossierRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Avocat;
import org.baeldung.persistence.model.pfe.Barreau;
import org.baeldung.persistence.model.pfe.BureauAvocat;
import org.baeldung.persistence.model.pfe.Client;
import org.baeldung.persistence.model.pfe.Dossier;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.baeldung.persistence.model.pfe.Tribunal;
import org.baeldung.persistence.model.pfe.TypeDossier;
import org.baeldung.persistence.model.pfe.Ville;
import org.baeldung.service.BureauAvocatService;
import org.baeldung.service.ClientService;
import org.baeldung.service.DossierService;
import org.baeldung.service.SecretaireService;
import org.baeldung.service.TypeDossierService;
import org.baeldung.service.AvocatService;
import org.baeldung.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClientController {
	@Autowired
	BureauAvocatService bureauAvocatService;
	@Autowired
	private ClientService clientService;  // Injection du service

	@Autowired
	private BureauAvocatRepository bureauAvocatRepository;
	@Autowired
	AvocatService avocatService;
	@Autowired
	UserService userService;
	@Autowired
	DossierService dossierService;
	@Autowired
	TypeDossierService typeDossierService;
	@Autowired
	SecretaireService secretaireService;;
	@Autowired
	TypeDossierRepository typeDossierRepository;
	@Autowired
	private ClientRepository clientRepository;

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

		System.out.println("usernnnnnnnnnnn:admin :" + user.getEmail());
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

//	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/dossier-list")
//	public ModelAndView wxAutoLogin16(HttpServletRequest request, HttpServletResponse response, Model model,
//			@RequestParam(name = "query", defaultValue = "") String mc,
//			@RequestParam(name = "qury", defaultValue = "") String mv,
//			@RequestParam(name = "queryDossier", defaultValue = "") String md) throws Exception {
//
//		System.err.println("dossier-list");
//
//		List<Dossier> dossiers = dossierService.findBynumeroDossier(md);
//
//		System.out.println("###" + dossiers.get(0).getTribunal().size());
//		List<Tribunal> tribunals = dossierService.findAllnom();
//
//		List<Avocat> avocat = avocatService.findByfirstName(mc);
//		List<Barreau> barreaux1 = avocatService.findBynomBarreau(mv);
//
//		List<Barreau> barreaux = avocatService.findAllBarreaux();
//		List<TypeDossier> typeDossierss = typeDossierService.findAllTypeDossier();
//		List<TypeDossier> typeDossiers = typeDossierService.findAll();
//
//		List<Ville> villes = avocatService.findAllVilles();
//		List<Avocat> avocats = avocatService.findAll();
//		/* List<Secretaire> secretaires =secretaireService.findAll(); */
//
//		Encoder encoder = Base64.getEncoder();
//		System.out.println(avocats.size());
//		model.addAttribute("typeDossiers", typeDossiers);
//		model.addAttribute("typeDossiers", typeDossierss);
//		model.addAttribute("tribunals", tribunals);
//		model.addAttribute("dossiers", dossiers);
//		/* model.addAttribute("dossiers",dossiers1); */
//		model.addAttribute("avocats", avocats);
//		model.addAttribute("barreaux", barreaux);
//		model.addAttribute("barreaux1", barreaux1);
//		model.addAttribute("villes", villes);
//		ModelAndView ret = new ModelAndView();
//		HttpSession session = request.getSession();
//		User user = (User) session.getAttribute("user1");
//		System.out.println("usernnnnnnnnnnn" + user.getEmail());
//		if (user != null) {
//			ret.addObject("user", user);
//		}
//		ret.setViewName("/Admin/AdminAvocat/Avocat/dossier-list");
//		return ret;
//	}

//	@RequestMapping(value = "/Admin/AdminAvocat/Avocat/AddFolder", method = RequestMethod.POST)
//	public String saveDossier(
//
//			@RequestParam("Dnum") String numeroDossier,
//
//			@RequestParam("Ddate") Date DateCreation,
//
//			@RequestParam("DtypeCas") String typeDecas,
//
//			@RequestParam("DtypeP") String typeProsedure,
//
//			@RequestParam("DdateP") Date dateProchSession,
//
//			@RequestParam("DnumN") String numeroNational,
//
//			@RequestParam("Dtype") Long Dtype,
//
//			@RequestParam("Dsujet") String sujet) {
//		System.err.println("ajoute dossier");
//
//		System.out.println(numeroDossier +" * "+DateCreation +" * "+typeDecas +" * "+typeProsedure +" * "+dateProchSession +" * "+Dtype +" * "+numeroNational +" * "+sujet);
//
//		TypeDossier type = new TypeDossier();
//		/* System.err.println(Dtype); */
//		type = typeDossierRepository.findTypeDossierById(Dtype);
//		System.err.println(type.getNom());
//
//		BureauAvocat bureauAvocat = bureauAvocatRepository.findAll().get(0);
//		Dossier d = new Dossier(numeroDossier, DateCreation, typeDecas, sujet, typeProsedure, dateProchSession,
//				numeroNational, type,bureauAvocat);
//		dossierService.addDossier(d);
//
//		return "redirect:/Admin/AdminAvocat/Avocat/dossier-list";
//	}
//
//	@Autowired
//	TypeDossierRepository typeDossierRepository;
//	@Autowired
//	BureauAvocatRepository bureauAvocatRepository;

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
		/*
		 * List<Dossier>
		 * dossiers=dossierService.findBynumeroDossierOrnumeroNational(md,mn);
		 */

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
		/*
		 * System.out.println(encoder.encodeToStringavocats.get(0).getImguser().getBytes
		 * ()));
		 */
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
		/*
		 * System.out.println(encoder.encodeToStringavocats.get(0).getImguser().getBytes
		 * ()));
		 */
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
