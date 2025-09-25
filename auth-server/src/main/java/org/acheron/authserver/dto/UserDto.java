package org.acheron.authserver.dto;

public record UserDto(
        String username,
        String email,
        String role
        ){
    public static UserDto toAnotherUserDto(org.acheron.user.UserDto userDto){
        return new UserDto(userDto.getUsername(), userDto.getEmail(), userDto.getRole());
    }
}

