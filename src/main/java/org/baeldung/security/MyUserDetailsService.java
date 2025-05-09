package org.baeldung.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
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

    public MyUserDetailsService() {
        super();
    }

    // API

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }

        try {
            final User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("No user found with username: " + email);
            }

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, getAuthorities(user.getRoles()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
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

            // Ajouter des logs pour déboguer
            System.out.println("Autorités générées:");
            for (String priv : privileges) {
                System.out.println("- " + priv);
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
