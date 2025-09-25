package com.acheron.mailserver.service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;

public class MailUtils {

    public static String getTextFromMessage(Message message) throws MessagingException, IOException {
        Object content = message.getContent();
        if (content instanceof String) {
            return (String) content; // simple text email
        } else if (content instanceof MimeMultipart) {
            return getTextFromMimeMultipart((MimeMultipart) content);
        }
        return "";
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            Object content = bodyPart.getContent();
            if (content instanceof String) {
                result.append((String) content);
            } else if (content instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) content));
            }
        }
        return result.toString();
    }
}
