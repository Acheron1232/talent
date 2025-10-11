package org.acheron.authserver.dto;

public record UserCreationDto(
        String username,
        String email,
        String password,
        boolean isEmailVerified,
        String role,
        String authMethod,
        boolean isMFAEnabled,
        String MFASecret
) {
}