package org.signature.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class MailUtil {

    private static final String sender_receiver = "***************@gmail.com";

    public static boolean sendMail(String user, String content) {
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();

            new Thread(() -> {
                String password = "**********";
                String host = "smtp.gmail.com";

                String subject = "Feedback from " + user;

                final Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", "587");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender_receiver, password);
                    }
                });

                Message message = prepareMessage(session, subject, content);
                if (message != null) {
                    try {
                        Transport.send(message);
                    } catch (MessagingException exception) {
                        System.out.println("Submission failed! " + exception.getLocalizedMessage());
                    }
                } else {
                    System.out.println("Failed to create message!");
                }
            }).start();

            return true;
        } catch (IOException exception) {
            System.out.println(exception.getLocalizedMessage());
            return false;
        }
    }

    private static Message prepareMessage(Session session, String subject, String content) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender_receiver));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(sender_receiver));
            message.setSubject(subject);
            message.setContent(content, "text/html");
            return message;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }
}
