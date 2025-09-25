package com.acheron.mailserver.service;

import com.acheron.mailserver.api.MailApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailHandler {
    private final MailService mailService;

    @KafkaListener(topics = {"mail"}, groupId = "mail")
    public void listen(String rawMail) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MailApi.MailDto mailDto = objectMapper.readValue(rawMail, MailApi.MailDto.class);
        log.info("Sending mail : {}", mailDto);
        mailService.sendMail(mailDto);
    }
}
