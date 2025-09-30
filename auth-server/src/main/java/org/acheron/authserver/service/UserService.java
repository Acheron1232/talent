package org.acheron.authserver.service;

import org.acheron.authserver.dto.UserCreateDto;
import org.acheron.authserver.dto.UserCreationDto;
import org.acheron.authserver.entity.User;
import org.acheron.user.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.rmi.RemoteException;
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


    public void saveOauthUser(UserCreateDto user) {
        OAuth2AuthorizeRequest request1 = OAuth2AuthorizeRequest
                .withClientRegistrationId("auth-server-service")
                .principal("auth-server")
                .build();
        OAuth2AuthorizedClient authorize = manager.authorize(request1);
        if ((user.user().password()!=null)){
            String encodedPassword =  passwordEncoder.encode(user.user().password());
        }
        // save user-service

        Long id = userGrpcClient.saveUser(user.user(), authorize.getAccessToken().getTokenValue());
        //save social-server
        RestClient.create().post().uri("http://localhost:8080/socials/profile").body(user.profile()).header("Authentication","Bearer "+authorize.getAccessToken().getTokenValue()).exchange((req,res)->{
            if(res.getStatusCode().value()>=200 &&res.getStatusCode().value()<300){

            }else {
                throw new RemoteException("Pizda");
            }
            return null;
        });

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
        return userGrpcClient.findUserByUsername(username, authorize.getAccessToken().getTokenValue());
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
