package org.baeldung.security.google2fa;

import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.pfe.SecretaireRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.pfe.Secretaire;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
//import org.springframework.stereotype.Component;

//@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecretaireRepository secretaireRepository;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        final User user = userRepository.findByEmail(auth.getName());
        final Secretaire secretaire = secretaireRepository.findByEmail(auth.getName());

        // Si ni l'utilisateur ni le secrétaire n'existe avec cet email
        if (user == null && secretaire == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Authentifier l'utilisateur s'il existe
        if (user != null) {
            if (!user.getEnabled()) {
               // throw new DisabledException("User is disabled");
            }

            // Vérifier le code 2FA si activé
            if (user.isUsing2FA()) {
                final String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
                final Totp totp = new Totp(user.getSecret());
                if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                    throw new BadCredentialsException("Invalid verification code");
                }
            }

            final Authentication result = super.authenticate(auth);
            return new UsernamePasswordAuthenticationToken(user, result.getCredentials(), result.getAuthorities());
        }

        // Authentifier le secrétaire s'il existe
        if (secretaire != null) {
            if (!secretaire.getEnabled()) {
                throw new DisabledException("Secretaire is disabled");
            }

            // Vérifier le code 2FA si activé
            if (secretaire.isUsing2FA()) {
                final String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
                final Totp totp = new Totp(secretaire.getSecret());
                if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                    throw new BadCredentialsException("Invalid verification code");
                }
            }

            final Authentication result = super.authenticate(auth);
            return new UsernamePasswordAuthenticationToken(secretaire, result.getCredentials(), result.getAuthorities());
        }

        // Ce code ne devrait jamais être atteint avec la logique ci-dessus
        throw new BadCredentialsException("Authentication error");
    }


//    @Override
//    public Authentication authenticate(Authentication auth) throws AuthenticationException {
//        final User user = userRepository.findByEmail(auth.getName());
//        if ((user == null)) {
//            throw new BadCredentialsException("Invalid username or password");
//        }
//        // to verify verification code
//        if (user.isUsing2FA()) {
//            final String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
//            final Totp totp = new Totp(user.getSecret());
//            if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
//                throw new BadCredentialsException("Invalid verfication code");
//            }
//
//        }
//        final Authentication result = super.authenticate(auth);
//        return new UsernamePasswordAuthenticationToken(user, result.getCredentials(), result.getAuthorities());
//    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
