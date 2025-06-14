package org.baeldung.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.pfe.SecretaireRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private SecretaireRepository secretaireRepository;

    public MyUserDetailsService() {
        super();
    }
@Override
public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
    final String ip = getClientIP();
    if (loginAttemptService.isBlocked(ip)) {
        throw new RuntimeException("blocked");
    }

    // Chercher dans la table des utilisateurs
    User user = userRepository.findByEmail(email);
    System.out.println(user);

    // Si l'utilisateur n'est pas trouvé, chercher dans la table des secrétaires
    if (user == null) {
        Secretaire secretaire = secretaireRepository.findByEmail(email);
        System.out.println(secretaire);
        if (secretaire != null && secretaire.getEnabled()) {
            // Créez un utilisateur temporaire basé sur les informations du secrétaire
            user = new User();
            user.setEmail(secretaire.getEmail());
            user.setFirstName(secretaire.getFirstName() != null ? secretaire.getFirstName() : "Default");
            user.setLastName(secretaire.getLastName() != null ? secretaire.getLastName() : "User");
            user.setPassword(secretaire.getPassword());  // Copier le mot de passe!
            user.setEnabled(true);
            try {
                List<Role> secretaireRoles = roleRepository.findRolesBySecretaireId(secretaire.getId());
                if (secretaireRoles != null && !secretaireRoles.isEmpty()) {
                    user.setRoles(secretaireRoles);
                } else {
                    // Fallback au cas où aucun rôle n'est trouvé
                    Role defaultRole = roleRepository.findByName("ROLE_SECRETAIRE");
                    if (defaultRole == null) {
                        defaultRole = roleRepository.findByName("ROLE_SECRETARY");
                    }

                    if (defaultRole != null) {
                        user.setRoles(Arrays.asList(defaultRole));
                    } else {
                        System.out.println("WARNING: Aucun rôle trouvé pour le secrétaire!");
                        throw new UsernameNotFoundException("Aucun rôle trouvé pour le secrétaire");
                    }
                }
            } catch (Exception e) {
                // Approche 2: Si la méthode ci-dessus n'existe pas, utilisez directement le rôle par nom
                Role secretaireRole = roleRepository.findByName("ROLE_SECRETAIRE");
                if (secretaireRole == null) {
                    secretaireRole = roleRepository.findByName("ROLE_SECRETARY");
                }

                if (secretaireRole != null) {
                    user.setRoles(Arrays.asList(secretaireRole));
                } else {
                    System.out.println("WARNING: Aucun rôle trouvé pour le secrétaire!");
                    throw new UsernameNotFoundException("Aucun rôle trouvé pour le secrétaire");
                }
            }
        } else {
            throw new UsernameNotFoundException("Aucun utilisateur ou secrétaire trouvé avec l'email: " + email);
        }
    }

    // Assurez-vous que l'utilisateur a des rôles valides
    if (user.getRoles() == null || user.getRoles().isEmpty()) {
        System.out.println(user.getRoles());
        throw new UsernameNotFoundException("L'utilisateur n'a pas de rôles attribués: " + email);
    }

    return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.getEnabled(),
            true, true, true,
            getAuthorities(user.getRoles())
    );
}


    private final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
            final List<String> privileges = new ArrayList<String>();
            final List<Privilege> collection = new ArrayList<Privilege>();

            // Convertir les rôles en format attendu par hasRole()
            for (final Role role : roles) {
                // Extraire le nom du rôle sans le préfixe ROLE_ s'il existe déjà
                String roleName = role.getName();
                if (roleName.startsWith("ROLE_")) {
                    privileges.add(roleName); // Déjà au bon format
                } else {
                    // Si le nom est au format XXX_PRIVILEGE, extraire le rôle
                    if (roleName.endsWith("_PRIVILEGE")) {
                        String baseRole = roleName.replace("_PRIVILEGE", "");
                        privileges.add("ROLE_" + baseRole);
                    } else {
                        // Sinon, ajouter simplement le préfixe ROLE_
                        privileges.add("ROLE_" + roleName);
                    }
                }

                // Ajouter également les privilèges (optionnel selon votre besoin)
                collection.addAll(role.getPrivileges());
            }

            // Si vous voulez aussi garder les privilèges originaux (optionnel)
            for (final Privilege item : collection) {
                privileges.add(item.getName());
            }

            return getGrantedAuthorities(privileges);
        }

    private final List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<String>();
        final List<Privilege> collection = new ArrayList<Privilege>();
        for (final Role role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private final String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
