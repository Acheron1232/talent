package com.acheron.mailserver.api;

//import com.acheron.mailserver.service.MailReceiver;
import com.acheron.mailserver.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MailApi {
    private final MailService mailService;
//    private final MailReceiver mailReceiver;

    @PostMapping
    public void sendMail(@RequestBody MailDto mail) throws MessagingException, IOException {
        mailService.sendMail(mail);
    }
//    @GetMapping
//    public void sendMail() throws MessagingException, IOException {
//        mailReceiver.receiver();
//    }

    public record MailDto(
      String to,
      String content,
      Long userId,
      String subject
    ){}
}
