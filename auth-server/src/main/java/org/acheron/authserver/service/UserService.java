package org.acheron.authserver.service;

import org.acheron.authserver.dto.UserCreateDto;
import org.acheron.authserver.dto.UserCreateOauthDto;
import org.acheron.authserver.dto.UserDto;
import org.acheron.authserver.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserGrpcClient userGrpcClient;
    private final OAuth2AuthorizedClientManager manager;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserGrpcClient userGrpcClient, OAuth2AuthorizedClientManager manager, PasswordEncoder passwordEncoder) {
        this.userGrpcClient = userGrpcClient;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
    }


    public void saveOauthUser(UserCreateOauthDto user) {
        OAuth2AuthorizeRequest request1 = OAuth2AuthorizeRequest
                .withClientRegistrationId("auth-server-service")
                .principal("auth-server")
                .build();
        OAuth2AuthorizedClient authorize = manager.authorize(request1);
        if ((user.getPassword()!=null)){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userGrpcClient.saveUser(new UserCreateDto(user.getUsername(), user.getEmail(), user.getPassword(), true,
                User.Role.USER.name(), user.getAuthMethod()), authorize.getAccessToken().getTokenValue());
    }


    public boolean existsByEmail(String email) {
        OAuth2AuthorizeRequest request1 = OAuth2AuthorizeRequest
                .withClientRegistrationId("auth-server-service")
                .principal("auth-server")
                .build();
        OAuth2AuthorizedClient authorize = manager.authorize(request1);
        return userGrpcClient.existsByEmail(email, authorize.getAccessToken().getTokenValue());
    }

    public UserDto findByUsername(String username) {
        OAuth2AuthorizeRequest request1 = OAuth2AuthorizeRequest
                .withClientRegistrationId("auth-server-service")
                .principal("auth-server")
                .build();
        OAuth2AuthorizedClient authorize = manager.authorize(request1);
        return UserDto.toAnotherUserDto(userGrpcClient.findUserByUsername(username, authorize.getAccessToken().getTokenValue()));
    }

    public Optional<User> findByEmail(String email) {
        OAuth2AuthorizeRequest request1 = OAuth2AuthorizeRequest
                .withClientRegistrationId("auth-server-service")
                .principal("auth-server")
                .build();
        OAuth2AuthorizedClient authorize = manager.authorize(request1);
        return Optional.of(User.userFromAnotherUserDto(userGrpcClient.findUserByEmail(email, authorize.getAccessToken().getTokenValue())));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
