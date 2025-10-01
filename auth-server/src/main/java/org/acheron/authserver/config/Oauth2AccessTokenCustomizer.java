package org.acheron.authserver.config;

import lombok.RequiredArgsConstructor;
import org.acheron.authserver.service.UserService;
import org.acheron.user.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Oauth2AccessTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UserService userService;

    @Override
    public void customize(JwtEncodingContext context) {
        if(!context.getRegisteredClient().getAuthorizationGrantTypes().equals(AuthorizationGrantType.AUTHORIZATION_CODE)){
            return;
        }
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            context.getClaims().claims(claims -> {
                Object principal = context.getPrincipal().getPrincipal();
                UserDto user;
                if (principal instanceof UserDetails) {
                    user = userService.findByUsername(((UserDetails) principal).getUsername());
                } else if (principal instanceof DefaultOAuth2User oidcUser) {
                    user = userService.findByUsername(
                            oidcUser.getAttribute("login") == null
                                    ? oidcUser.getAttribute("name")
                                    : oidcUser.getAttribute("login"));
                } else {
                    return;
                }
                claims.put("id", user.getId());
                claims.put("roles", user.getRole());
                claims.put("name", user.getUsername());
            });
        }
    }
}