package org.baeldung.spring;

import java.util.*;

import org.baeldung.persistence.dao.PrivilegeRepository;
import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.pfe.SecretaireRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.*;
import org.baeldung.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SecretaireRepository secretaireRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientService clientService;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

// == create initial roles with specific privileges

// Role for Avocat
        final List<Privilege> avocatPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role avocatRole = createRoleIfNotFound("ROLE_AVOCAT", avocatPrivileges);

// Role for Secrétaire
        final List<Privilege> secretairePrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role secretaireRole = createRoleIfNotFound("ROLE_SECRETAIRE", secretairePrivileges);

// Role for Client
        final List<Privilege> clientPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role clientRole = createRoleIfNotFound("ROLE_CLIENT", clientPrivileges);


        // == create initial roles
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        final Role UserRole = createRoleIfNotFound("ROLE_USER", userPrivileges);

        // == create initial user
       // createUserIfNotFound("salim@gmail.com", "soulayman","9ala", "1234", new ArrayList<Role>(Arrays.asList(UserRole)));
        createUserIfNotFound("chakirfst@gmail.com", "lo9man","chakiry", "1234", new ArrayList<Role>(Arrays.asList(UserRole)));
        createUserIfNotFound("bens@yahoo.com", "khawla", "bn", "abcd", new ArrayList<Role>(Arrays.asList(adminRole)));
        createUserIfNotFound("bilalelkhalabi93@gmail.com", "bilal", "khallabi", "1234", new ArrayList<Role>(Arrays.asList(adminRole)));
        createUserIfNotFound("kamal@yahoo.com","salamat","kamal","abcd", new ArrayList<Role>(Arrays.asList(adminRole)));
        createUserIfNotFound("idriss@yahoo.com","3issawi","idriss","abcd", new ArrayList<Role>(Arrays.asList(adminRole)));
        createUserIfNotFound("hamza@yahoo.com","hamzawi","hamza","abcd", new ArrayList<Role>(Arrays.asList(adminRole)));
        createUserIfNotFound("fatima@gmail.com","نجاح","فاطمة","abcd", new ArrayList<Role>(Arrays.asList(adminRole)));
        createUserIfNotFound("soufi@gmail.com", "hassan","chakir", "abcd", new ArrayList<Role>(Arrays.asList(UserRole)));
        createUserIfNotFound("chakirfst1@gmail.com", "lo9mann","chakiryy", "1234", new ArrayList<Role>(Arrays.asList(UserRole)));
        createUserIfNotFound("bens2@yahoo.com", "khawla2", "bn2", "1234", new ArrayList<Role>(Arrays.asList(adminRole)));
        createUserIfNotFound("bens3@yahoo.com", "bilal", "bbbbbbbbb", "1234", new ArrayList<Role>(Arrays.asList(adminRole)));

        // == create initial users and assign roles
        createUserIfNotFound("avocat@example.com", "John", "Doe", "avocatpassword", new ArrayList<Role>(Arrays.asList(avocatRole)));
        createUserIfNotFound("secretaire@example.com", "Marie", "Dupont", "secretairepassword", new ArrayList<Role>(Arrays.asList(secretaireRole)));
        createUserIfNotFound("client@example.com", "Paul1", "Martin1", "abcd", new ArrayList<Role>(Arrays.asList(clientRole)));
        createUserIfNotFound("bilal@example.com", "Paul", "Martin", "abcd", new ArrayList<Role>(Arrays.asList(clientRole)));
        createUserIfNotFound("secretaire1@example.com", "secr", "Dupontt", "1234", new ArrayList<Role>(Arrays.asList(secretaireRole)));
        createScretaireIfNotFound ("sec@example.com", "secr", "Dupontt", "1234", new ArrayList<Role>(Arrays.asList(secretaireRole)));

        createScretaireIfNotFound ("khallabi@gmail.com", "secretaire", "Dupontt", "1234", new ArrayList<Role>(Arrays.asList(secretaireRole)));
        createScretaireIfNotFound ("khallabii@gmail.com", "secretaire1", "Dupontt1", "1234", new ArrayList<Role>(Arrays.asList(secretaireRole)));
        createScretaireIfNotFound ("bilals@gmail.com", "bbbbb", "hghh", "1234", new ArrayList<Role>(Arrays.asList(secretaireRole)));
        createUserIfNotFound("bilalc@example.com", "Paul", "Martin", "1234", new ArrayList<Role>(Arrays.asList(clientRole)));
        createScretaireIfNotFound ("jaouadchtioui@gmail.com", "جواد", "الشتيوي", "1234", new ArrayList<Role>(Arrays.asList(secretaireRole)));
        createScretaireIfNotFound ("bilalkhallabi@gmail.com", "بلال", "الخلابي", "1234", new ArrayList<Role>(Arrays.asList(secretaireRole)));
        createClientIfNotFound ("bilalkhallabic@gmail.com", "بلال", "الخلابي", "1234", new ArrayList<Role>(Arrays.asList(clientRole)));
        createClientIfNotFound ("jaouadchtiouic@gmail.com", "jaouad", "chtioui", "1234", new ArrayList<Role>(Arrays.asList(clientRole)));

        //createClientIfNotFound ("bilalkhallabia@gmail.com", "بلال", "الخلابي", "1234", new ArrayList<Role>(Arrays.asList(avocatRole)));
        createAvocatIfNotFound ("bilalkhallabiav@gmail.com", "بلال", "الخلابي", "1234", new ArrayList<Role>(Arrays.asList(avocatRole)));
        alreadySetup = true;
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    public User createUserIfNotFound( String email,  String firstName,  String lastName,  String password,  Collection<Role> roles) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }
    @Transactional
    public User createClientIfNotFound( String email,  String firstName,  String lastName,  String password,  Collection<Role> roles) {
        Client client = clientService.findByEmail(email);
        if (client == null) {
            client = new Client();
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setPassword(passwordEncoder.encode(password));
            client.setEmail(email);
            client.setEnabled(true);
        }
        client.setRoles(roles);
        client = userRepository.save(client);
        return client;
    }

    @Transactional
    public Avocat createAvocatIfNotFound(String email, String firstName, String lastName, String password,
                                         Collection<Role> roles) {
        // Vérifiez si l'avocat existe déjà
        User existingUser = userRepository.findByEmail(email);
        Avocat avocat;

        if (existingUser == null) {
            avocat = new Avocat();
            avocat.setFirstName(firstName);
            avocat.setLastName(lastName);
            avocat.setPassword(passwordEncoder.encode(password));
            avocat.setEmail(email);
            avocat.setEnabled(true);
            // Initialiser les collections vides
            avocat.setDossier(new HashSet<>());
            avocat.setClient(new ArrayList<>());
        } else if (existingUser instanceof Avocat) {
            avocat = (Avocat) existingUser;
        } else {
            // Si l'utilisateur existe mais n'est pas un avocat, on pourrait lancer une exception
            // ou créer un nouvel avocat avec un email légèrement différent
            throw new IllegalStateException("Un utilisateur avec cet email existe déjà mais n'est pas un avocat.");
        }

        avocat.setRoles(roles);
        return userRepository.save(avocat);
    }
    @Transactional
    public User createScretaireIfNotFound( String email,  String firstName,  String lastName,  String password,  Collection<Role> roles) {
        Secretaire secretaire = secretaireRepository.findByEmail(email);
        if (secretaire == null) {
            secretaire = new Secretaire();
            secretaire.setFirstName(firstName);
            secretaire.setLastName(lastName);
            secretaire.setPassword(passwordEncoder.encode(password));
            secretaire.setEmail(email);
            secretaire.setEnabled(true);
        }
        secretaire.setRoles(roles);
        secretaire = secretaireRepository.save(secretaire);
        return secretaire;
    }

}