package org.acheron.authserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.acheron.authserver.dto.ProfileCreationDTO;
import org.acheron.authserver.dto.UserCreateDto;
import org.acheron.authserver.dto.UserCreationDto;
import org.acheron.authserver.entity.User;
import org.acheron.authserver.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2Token)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String provider = oAuth2Token.getAuthorizedClientRegistrationId();
        if (!"github".equals(provider) && !"google".equals(provider)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = new HashMap<>(principal.getAttributes());

        String email;
        String avatar = null;
        try {
            if ("github".equals(provider)) {
                email = ((String) attributes.getOrDefault("email", ""));
                if (email == null) {
                    email = getPrimaryEmailForGitHub(authentication);
                    avatar = attributes.getOrDefault("avatar_url", null).toString();
                }
            } else {
                email = attributes.getOrDefault("email", "").toString();
                avatar = attributes.getOrDefault("picture", null).toString();
                if (email.isEmpty()) {
                    throw new IllegalStateException("Email not found in Google attributes");
                }
            }
        } catch (Exception e) {
            log.error("Failed to get email from OAuth2 provider", e);
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        if (!userService.existsByEmail(email)) {
            String username = (String) attributes.get("name");
            UserCreationDto newUser = new UserCreationDto(username,email,null,true, User.Role.USER.toString(),"github".equals(provider) ? "GITHUB" : "GOOGLE",false,null);
            UserCreateDto dto = new UserCreateDto(new ProfileCreationDTO(null,username,username,avatar), newUser); //TODO
            userService.saveOauthUser(dto);
        }

        log.info("User logged in via {}: {}", provider, email);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String getPrimaryEmailForGitHub(Authentication authentication) throws JsonProcessingException {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(),
                        authentication.getName());
        String accessToken = client.getAccessToken().getTokenValue();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "https://api.github.com/user/emails";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String json = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> emails = objectMapper.readValue(json, new TypeReference<>() {
        });
        return emails.stream()
                .filter(email -> Boolean.TRUE.equals(email.get("primary")))
                .map(email -> (String) email.get("email"))
                .findFirst()
                .orElseThrow();
    }
}
