package org.acheron.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.image.BufferedImage;

@Configuration
public class UtilConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public HttpMessageConverter<BufferedImage> imageConverter()  {
        return new BufferedImageHttpMessageConverter();
    }
}
