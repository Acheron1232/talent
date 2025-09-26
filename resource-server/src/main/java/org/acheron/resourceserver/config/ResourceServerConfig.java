    package org.acheron.resourceserver.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    @Configuration
    @EnableWebSecurity
    public class ResourceServerConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.cors(e->e.disable());
            http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/books").authenticated());
            http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
            return http.build();
        }
    }