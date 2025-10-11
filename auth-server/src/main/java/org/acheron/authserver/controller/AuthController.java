package org.acheron.authserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.acheron.authserver.dto.ProfileCreationDTO;
import org.acheron.authserver.dto.UserCreateDto;
import org.acheron.authserver.dto.UserCreationDto;
import org.acheron.authserver.entity.User;
import org.acheron.authserver.service.QrCodeService;
import org.acheron.authserver.service.TokenGrpcClient;
import org.acheron.authserver.service.UserService;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final TokenGrpcClient tokenGrpcClient;
    private final QrCodeService qrCodeService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")

    public String registrationApi(@RequestParam Map<String, String> params) {
        String mfa_secret = null;
        if (params.getOrDefault("mfa_enabled", "false").equals("true")) {
            mfa_secret = Base32.random();
        }
        userService.saveOauthUser(
                new UserCreateDto(new ProfileCreationDTO(
                        null,
                        params.get("username"), //TODO
                        params.get("username"),
                        "https://firebasestorage.googleapis.com/v0/b/container-61d6e.firebasestorage.app/o/static%2Fdefaul_user.png?alt=media&token=99f3e797-d1ed-4efc-aae5-b56d1b94c15d"
                ),
                        new UserCreationDto(
                                params.get("username"),
                                params.get("email"),
                                params.get("password"),
                                false,
                                User.Role.USER.toString(),
                                User.AuthMethod.DEFAULT.toString(),
                                Boolean.parseBoolean(params.getOrDefault("mfa_enabled","false")),
                                mfa_secret

                )));
        return "redirect:/login";
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

    @GetMapping(value = "/mfa_qr",produces = {MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public BufferedImage getQrCode() {
        System.out.println("asd");
        return qrCodeService.generateQrCode("Talent","aryemfedorov@gmail.com","K4RJK7LR3FFUSTCG");
    }

}