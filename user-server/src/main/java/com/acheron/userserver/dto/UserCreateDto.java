package com.acheron.userserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    private String username;
    private String password;
    private String email;
    private String displayName;
    private String image;
    private boolean isEmailVerified;
    private String role;
    private String authMethod;
}