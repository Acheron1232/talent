package org.acheron.authserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateOauthDto {
    String username;
    String email;
    String password;
    String displayName;
    String image;
    boolean isEmailVerified;
    String role;
    String authMethod;
}
