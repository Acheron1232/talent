package com.acheron.userserver.api;

import com.acheron.userserver.service.AuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TokenApi {
    private final AuthHandler authHandler;

    @PostMapping("/confirmEmail")
    public ResponseEntity<String> confirmEmail(@AuthenticationPrincipal Jwt jwt) {
        return authHandler.confirmEmail((String) jwt.getClaims().get("name"));
    }
    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return authHandler.confirm(token);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody EmailDto email) {
        return authHandler.resetPassword(email.email);
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<String> reset(@RequestParam("token") String token) {
        return ResponseEntity.ok(authHandler.reset(token));
    }

    public record EmailDto(String email) {}
}
