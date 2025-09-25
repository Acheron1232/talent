package com.acheron.mailserver.service;

import com.acheron.mailserver.api.MailApi;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.NewsAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.thymeleaf.expression.Maps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(MailApi.MailDto mail)  {
        try{

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom("no-reply@pizzeria.com");
            helper.setTo(mail.to());
            helper.setSubject(mail.subject());
//            String htmlContent = loadHtmlTemplate("index.html");
//            Map<String, Object> models = new HashMap<>();
//            models.put("name", mail.userId());
//            for (Map.Entry<String, Object> entry : models.entrySet()) {
//                htmlContent = htmlContent.replace("${" + entry.getKey() + "}", entry.getValue().toString());
//            }
//        List<String> lines = Files.readAllLines(Path.of("src/main/java/com/acheron/flowers/mail/templates/verifier.html"));
//        String htmlTemplate = lines.stream().reduce((e1,e2)-> e1+"\n"+e2).orElseThrow();
//        htmlTemplate = htmlTemplate.replace("${ref}", ref);
            helper.setText(mail.content(), true);
            mailSender.send(message);
        }catch (MessagingException e){
            log.error(e.getMessage());
        }
    }

    private String loadHtmlTemplate(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + fileName);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
