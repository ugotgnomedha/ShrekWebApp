package com.example.LoginRegister.RegisterProcess;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailAuth {
    private static final Logger logger = LogManager.getLogger(EmailAuth.class);

    public static void sendAuthEmail(String userEmail) {
        String from = "FiltreyshnTeam@yandex.ru";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        //props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.yandex.ru");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.port", "587");

//        final String username = "FiltreyshnTeam@yandex.ru";
//        final String password = "sawseb-jIswa1-vowpeb";
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "sawseb-jIswa1-vowpeb");
            }
        });

        session.setDebug(true);

        // Create message.
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Testing Subject");
            message.setText("Dear Mail Crawler,"
                    + "\n\n No spam to my email, please!");

            Transport.send(message);
            System.out.println("Sent email");
        } catch (MessagingException mex) {
            logger.error("Error occured while sending email to: " + userEmail + "", mex);
        }

    }
}
