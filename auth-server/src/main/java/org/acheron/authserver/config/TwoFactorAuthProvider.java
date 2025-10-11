package org.acheron.authserver.config;

import org.acheron.authserver.config.util.CustomWebAuthenticationDetails;
import org.acheron.authserver.entity.User;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class TwoFactorAuthProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("TwoFactorAuthProvider.authenticate");
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Object principal = authentication.getPrincipal();
            if(principal instanceof UsernamePasswordAuthenticationToken){
                User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
                CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) ((UsernamePasswordAuthenticationToken) principal).getDetails();
                if (user.getIsMFAEnabled()){
                    String code = details.getVerificationCode();
                    if (code!=null && !code.isBlank()) {
                        Totp totp = new Totp(user.getMFASecret());
                        if (!isValidLong(code) || !totp.verify(code)) {
                            throw new BadCredentialsException("Invalid verfication code"); //TODO

                        }
                    }else {
                        throw new RuntimeException("2fa code is null");
                    }
                }else {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public TwoFactorAuthProvider(){
        super();
    }
}
