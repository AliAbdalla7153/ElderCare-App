package com.example.eldercareapp;

import android.util.Log;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends Thread {
    private static final String TAG = "JavaMailAPI";
    private final String recipientEmail;
    private final String subject;
    private final String body;

    public JavaMailAPI(String recipientEmail, String subject, String body) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
    }

    @Override
    public void run() {
        final String senderEmail = "eldercareg2@gmail.com";
        // Ensure this is a valid 16-character Google App Password
        final String senderPassword = "lgmtweopxmrgmkvn";

        Log.d(TAG, "Starting email send to: " + recipientEmail);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        // Timeout settings
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "Eldercare Helper"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            
            // Optional: CC the sender for verification
            message.setRecipients(
                    Message.RecipientType.CC,
                    InternetAddress.parse(senderEmail)
            );
            
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            Log.d(TAG, "Email sent successfully to " + recipientEmail);
        } catch (Exception e) {
            Log.e(TAG, "Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
