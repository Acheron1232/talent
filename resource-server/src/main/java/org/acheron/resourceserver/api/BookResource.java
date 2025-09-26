package org.acheron.resourceserver.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
public class BookResource {

    @GetMapping("/books")
    public ResponseEntity<String> getBooks(Authentication authentication, HttpServletRequest request) {
        assert authentication instanceof JwtAuthenticationToken;
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        String username = authentication.getName();
        String jwtString = jwtAuthenticationToken.getToken().getTokenValue();

        RestClient restClient = RestClient.builder().build();
        Map<String, String> user = restClient.method(HttpMethod.GET).uri("http://localhost:8080/user/userinfo")
                .header("Authorization", "Bearer " + jwtString)
                .accept(APPLICATION_JSON).retrieve().body(new ParameterizedTypeReference<>() {
                });

        return ResponseEntity.ok("Hi " + user.get("name") + ", here are some books [book1, book2],\n"
                + "Also here is your jwt: " + jwtString
                + "\nAnd call to /user/current returned: " + user);
    }
}
