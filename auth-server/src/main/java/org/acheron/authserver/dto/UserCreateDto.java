package org.acheron.authserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCreateDto {
    String username;
    String email;
    String password;
    boolean isEmailVerified;
    String role;
    String authMethod;
}
