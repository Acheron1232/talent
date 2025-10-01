package org.acheron.authserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.acheron.authserver.dto.ProfileCreationDTO;
import org.acheron.authserver.dto.UserCreateDto;
import org.acheron.authserver.dto.UserCreationDto;
import org.acheron.authserver.entity.User;
import org.acheron.authserver.service.TokenGrpcClient;
import org.acheron.authserver.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final TokenGrpcClient tokenGrpcClient;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    @ResponseBody
    public void registrationApi(@RequestParam Map<String, String> params) {
        userService.saveOauthUser(
                new UserCreateDto(new ProfileCreationDTO(
                        null,
                        params.get("displayName"),
                        params.get("tag"),
                        params.get("profilePictureUrl")
                ),
                        new UserCreationDto(
                                params.get("username"),
                                params.get("email"),
                                params.get("password"),
                                false,
                                User.Role.USER.toString(),
                                User.AuthMethod.DEFAULT.toString())
                ));
    }

    @GetMapping("/spa/logout")
    @ResponseBody
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam String email) {

        tokenGrpcClient.resetPassword(email);
        return "login";


    }

    @GetMapping("/reset_password_token")
    @ResponseBody
    public ResponseEntity<String> reset(@RequestParam("token") String token) {
        return ResponseEntity.ok(tokenGrpcClient.reset(token));
    }

    @GetMapping("/reset_password")
    public String resetPassword() {
        return "reset_password";
    }
}