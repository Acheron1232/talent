package org.acheron.authserver.service;

import lombok.RequiredArgsConstructor;
import org.acheron.authserver.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHandler {
    private final OAuth2AuthorizedClientManager manager;

    public ResponseEntity<String> resetPassword(String email) {
        OAuth2AuthorizeRequest request1 = OAuth2AuthorizeRequest
                .withClientRegistrationId("auth-server-service")
                .principal("auth-server")
                .build();
        OAuth2AuthorizedClient authorize = manager.authorize(request1);
//        tokenGrpcService
        return ResponseEntity.ok("Email sent to reset password successfully");
    }
}
