package com.example.LoginRegister.RegisterProcess;

import com.example.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;

public class EmailAuth {
    private static final Logger logger = LogManager.getLogger(EmailAuth.class);

    private static void assignVerificationCode(String email, String verificationCode, String password, String userName) {
        try {
            Connection connection = DriverManager.getConnection(Constants.url, Constants.user, Constants.password);
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO registered_users(email, verification_code, password, user_name, verification_status) VALUES('" + email + "', '" + verificationCode + "', '"+password+"', '"+userName+"', 'not_verified')" +
                    " ON CONFLICT (email) DO UPDATE SET verification_code = '" + verificationCode + "'");

            statement.close();
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    public static void sendAuthEmail(String userEmail, String userName, String password) {
        //        final String username = "FiltreyshnTeam@yandex.ru";
        //        final String password = "sawseb-jIswa1-vowpeb";
        // BACK-UP EMAIL.

        String from = "filtreyshnteam@gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.debug", "false");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "securepassword123");
            }
        });

        session.setDebug(true);

        // Create message.
        try {
            //  Generate random 6-digit code.
            Random rnd = new Random();
            int number = rnd.nextInt(999999);
            String code = String.format("%06d", number);
            assignVerificationCode(userEmail, code, password, userName);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Подтверждение почты");
            message.setContent("<h2>Dear " + userName + ", here is your authentication code:</h2><br><h1>" + code + "</h1>", "text/html");
            Transport.send(message);
        } catch (MessagingException mex) {
            logger.error("Error occured while sending email to: " + userEmail + "", mex);
        }

    }
}
