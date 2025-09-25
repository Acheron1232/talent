//package com.acheron.mailserver.service;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.MimeMultipart;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Properties;
//
//@Service
//@EnableScheduling
//public class MailReceiver {
//
//    @Scheduled(fixedRate = 10000)
//    public Store receiver() throws MessagingException, IOException {
//        Properties props = System.getProperties();
//        props.setProperty("mail.store.protocol", "imaps");
//
//        Session session = Session.getDefaultInstance(props, null);
//
//        Store store = session.getStore("imaps");
//        store.connect("imap.googlemail.com", "aryemfedorov@gmail.com", "yucr xdzw suww yema");
////        Folder[] folders = store.getDefaultFolder().list("*");
////        for (Folder folder : folders) {
////            System.out.println(folder.getFullName());
////        }
//        Folder inbox = store.getFolder("[Gmail]/Spam");
//        inbox.open(Folder.READ_ONLY);
//        Message[] messages = inbox.getMessages();
//        System.out.println(messages.length);
//        if (messages.length > 0) {
//            Message message = messages[messages.length - 1];
//            System.out.println("Subject: " + message.getSubject());
//            System.out.println("From: " + Arrays.toString(message.getFrom()));
//            System.out.println("Text: " + MailUtils.getTextFromMessage(message));
//        }
//        store.close();
//        return store;
//    }
//}
