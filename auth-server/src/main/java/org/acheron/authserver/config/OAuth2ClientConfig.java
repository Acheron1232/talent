//package org.acheron.authserver.config;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.client.*;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
//import org.springframework.web.client.RestClient;
//
//@Configuration
//public class OAuth2ClientConfig {
//
//    @Bean
//    OAuth2AuthorizedClientManager authorizedClientManager(
//            ClientRegistrationRepository clientRegistrationRepository,
//            OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//        OAuth2AuthorizedClientProvider authorizedClientProvider =
//                OAuth2AuthorizedClientProviderBuilder.builder()
//                        .clientCredentials()
//                        .build();
//
//        DefaultOAuth2AuthorizedClientManager manager =
//                new DefaultOAuth2AuthorizedClientManager(
//                        clientRegistrationRepository,
//                        authorizedClientRepository
//                );
//
//        manager.setAuthorizedClientProvider(authorizedClientProvider);
//        return manager;
//    }
//
//    @Bean
//    @Qualifier("rest-client-with-auth")
//    RestClient restClient(@Value("${spring.base-url:http://localhost:8080}/user") String baseUrl, OAuth2AuthorizedClientManager manager) {
//        return RestClient.builder()
//                .baseUrl(baseUrl)
//                .requestInterceptor((request, body, execution) -> {
//                    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
//                            .withClientRegistrationId("auth-server-service")
//                            .principal("auth-server")
//                            .build();
//
//                    OAuth2AuthorizedClient client = manager.authorize(authorizeRequest);
//                    if (client != null) {
//                        request.getHeaders().setBearerAuth(
//                                client.getAccessToken().getTokenValue()
//                        );
//                    }
//
//                    return execution.execute(request, body);
//                })
//                .build();
//    }
//}
