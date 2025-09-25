package com.acheron.userserver.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic mailTopic() {
        return new NewTopic("mail", 3, (short) 1);
    }
}
