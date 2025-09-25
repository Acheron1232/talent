package com.acheron.userserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public  void sendEmail(MailDto mail)  {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String value = objectMapper.writeValueAsString(mail);
            kafkaTemplate.send("mail", value);
            log.info("Sending mail : {}", mail);
        }catch (JsonProcessingException e){
            log.error(e.getMessage());
        }
    }

    public record MailDto(String to, String subject, String content) {
    }

}
