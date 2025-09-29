package com.mykyda.talantsocials.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(e -> e.requestMatchers("/profile/get-by-tag/",
                                "/posts/get-posts/",
                                "/posts/get-posts/",
                                "/comments/get-comments/",
                                "/comments/get-replies/")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(e -> e.jwt(Customizer.withDefaults()))
                .build();
    }
}
