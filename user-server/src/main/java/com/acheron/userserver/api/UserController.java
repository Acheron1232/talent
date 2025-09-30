package com.acheron.userserver.api;

import com.acheron.userserver.dto.UserChangeDto;
import com.acheron.userserver.dto.UserDto;
import com.acheron.userserver.entity.User;
import com.acheron.userserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/userinfo")
    public UserDto getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.findByUsername((String) jwt.getClaims().get("name"))
                .orElseThrow(() -> new UsernameNotFoundException((String) jwt.getClaims().get("username")));
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    @PatchMapping("/userinfo")
    public UserChangeDto changeUserProp(@AuthenticationPrincipal Jwt jwt) {
        return null;
    }

    @PutMapping("/userinfo")
    public UserChangeDto changeUserProps(@AuthenticationPrincipal Jwt jwt) {
        return null;
    }
}