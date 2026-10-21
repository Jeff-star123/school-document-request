package com.phinma.docrequest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        // Safe dispatch: prints to terminal if SMTP is not connected
        System.out.println("=================================================");
        System.out.println("📧 [PHINMA EMAIL NOTIFICATION]");
        System.out.println("TO: " + toEmail);
        System.out.println("SUBJECT: " + subject);
        System.out.println("MESSAGE:\n" + body);
        System.out.println("=================================================");

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
            } catch (Exception e) {
                System.err.println("Notice: SMTP not configured or failed. Logged to console instead: " + e.getMessage());
            }
        }
    }
}