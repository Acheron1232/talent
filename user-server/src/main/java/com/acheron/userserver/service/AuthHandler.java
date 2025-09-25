package com.acheron.userserver.service;

import com.acheron.userserver.entity.Token;
import com.acheron.userserver.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthHandler {
    private final TokenService tokenService;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    @Value("${server.port}") //TODO
    private int port;

//    public ResponseEntity<String> confirmEmail(String username) throws IOException {
//        User user = userService.findByName(username);
//        if (user.getIsEmailVerified()){
//            return ResponseEntity.ok("Email verified");
//        }
//        Token token = tokenService.generateConfirmationToken(user);
//        String html = new ClassPathResource("static/confirmation.html").getContentAsString(StandardCharsets.UTF_8).replaceFirst("urll",serverDomain+"/confirm?token="+token.getToken());
//        emailService.sendEmail(user.getEmail(), html,"Confirm email");
//        return ResponseEntity.ok("Email sent successfully");
//    }
//
//    public ResponseEntity<String> confirm(String token) {
//        tokenService.getToken(token).ifPresentOrElse(
//                (confirmationToken)->{
//                    if (!confirmationToken.getTokenType().equals(Token.TokenType.CONFIRM)){
//                        throw new BadCredentialsException("Token is not confirm");
//                    }
//                    if(confirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
//                        User user = confirmationToken.getUser();
//                        user.setIsEmailVerified(true);
//                        userService.update(user);
//                        tokenService.delete(confirmationToken);
//                    }else {
//                        throw new BadCredentialsException("Token expired");
//                    }
//                },()->{
//                    throw new BadCredentialsException("Invalid token");
//                }
//        );
//        return ResponseEntity.ok("Email confirmed");
//    }

    public ResponseEntity<String> resetPassword(String email) {
        try {

            boolean b = userService.existsByEmail(email);
            if (!b){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email does not register");
            }
            User user = userService.findByEmail(email).orElseThrow();
            Token token = tokenService.generateResetToken(user);
            String html = new ClassPathResource("static/reset_password.html").getContentAsString(StandardCharsets.UTF_8).replaceFirst("urll","http://127.0.0.1:"+"9000" +"/reset_password_token?token="+token.getToken());
            emailService.sendEmail(new EmailService.MailDto(user.getEmail(),"Reset password",html));
            return ResponseEntity.ok("Reset password sent successfully");
        }catch (IOException e){
            log.error(e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public String reset(String token) {
        AtomicReference<String> newPassword = new AtomicReference<>();
        tokenService.getToken(token).ifPresentOrElse(
                (confirmationToken)->{
                    if (!confirmationToken.getTokenType().equals(Token.TokenType.RESET)){
                        throw new BadCredentialsException("Token is not reset");
                    }
                    if(confirmationToken.getExpiredAt().isAfter(LocalDateTime.now())){
                        String substring = UUID.randomUUID().toString().substring(0, 10);
                        String password = passwordEncoder.encode(substring);
                        User user = confirmationToken.getUser();
                        user.setPassword(password);
                        userService.update(user);
                        tokenService.delete(confirmationToken);
                        newPassword.set(substring);

                    }else {
                        throw new BadCredentialsException("Token expired");
                    }
                },()->{
                    throw new BadCredentialsException("Invalid token");
                }
        );
        return newPassword.get();
    }
}
