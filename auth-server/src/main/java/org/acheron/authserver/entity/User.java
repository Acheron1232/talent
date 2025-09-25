package org.acheron.authserver.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    private Long id;
    private String email;
    private String username;
    private Boolean isEmailVerified;
    private AuthMethod authMethod;
    private Role role;
    private String password;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static User userFromAnotherUserDto(org.acheron.user.UserDto userDto){
        return new User(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getUsername(),
                userDto.getIsEmailVerified(),
                AuthMethod.valueOf(
                        userDto.getAuthMethod()),
                Role.valueOf(userDto.getRole()),
                userDto.getPassword().getValue());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public enum Role implements GrantedAuthority {
        USER, ADMIN;
        @Override
        public String getAuthority() {
            return name();
        }
    }

    public enum AuthMethod {
        DEFAULT, GOOGLE, GITHUB

    }
}
