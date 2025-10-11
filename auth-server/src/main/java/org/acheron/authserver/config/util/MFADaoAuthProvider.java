package org.acheron.authserver.config.util;

import org.acheron.authserver.entity.User;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MFADaoAuthProvider extends DaoAuthenticationProvider {
    public MFADaoAuthProvider(UserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);
        CustomWebAuthenticationDetails details =
                (CustomWebAuthenticationDetails) authentication.getDetails();
        User user = (User) userDetails;

        if (user.getIsMFAEnabled()) {
            String code = details.getVerificationCode();
            if (code == null || code.isBlank()) {
                throw new BadCredentialsException("2FA code is missing");
            }

            Totp totp = new Totp(user.getMFASecret());
            if (!isValidLong(code) || !totp.verify(code)) {
                throw new BadCredentialsException("Invalid verification code");
            }
        }
    }
    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
