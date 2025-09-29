package org.acheron.authserver.dto;

public record UserDto(
        Long id,
        String username,
        String email,
        String role
        ){
    public static UserDto toAnotherUserDto(org.acheron.user.UserDto userDto){
        return new UserDto(userDto.getId(),userDto.getUsername(), userDto.getEmail(), userDto.getRole());
    }
}

