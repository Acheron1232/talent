package com.acheron.userserver.api;

import com.acheron.userserver.dto.UserCreateDto;
import com.acheron.userserver.entity.User;
import com.acheron.userserver.service.AuthHandler;
import lombok.RequiredArgsConstructor;
import com.acheron.userserver.dto.UserChangeDto;
import com.acheron.userserver.dto.UserDto;
import com.acheron.userserver.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthHandler authHandler;

    @PostMapping("/")
    public void save(@RequestBody UserCreateDto userDto) {
        userService.save(userDto);
    }


    @GetMapping("/userinfo")
    public UserDto getCurrentUser(@AuthenticationPrincipal Jwt jwt){
        User user = userService.findByName((String) jwt.getClaims().get("name")).orElseThrow(() -> new UsernameNotFoundException((String) jwt.getClaims().get("username")));
        return new UserDto(user.getId(),user.getUsername(),user.getEmail(),user.getRole());
    }
    @PatchMapping("/userinfo")
    public UserChangeDto userChange(Principal principal){
        return null;
    }

//    @PostMapping("/confirmEmail")
//    public ResponseEntity<String> confirmEmail(@AuthenticationPrincipal Jwt jwt) {
//        return authHandler.confirmEmail((String) jwt.getClaims().get("name"));
//    }
//    @GetMapping("/confirm")
//    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
//        return authHandler.confirm(token);
//    }

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
