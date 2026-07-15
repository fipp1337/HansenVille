package com.hansenvillage.hansenapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Code registration confirm | Hansen Village");
        message.setText(String.format(
                "Hi!\n\n" +
                        "Your registration confirmation code for Hansen Village Application: %s\n" +
                        "This code is valid for 10 minutes.\n\n" +
                        "If you did not make this request, simply ignore this email.",
                code
        ));
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Forgot password code | Hansen Village");
        message.setText(String.format(
                "Hello!\n\n" +
                        "You have requested a password reset for your Hansen Village account.\n" +
                        "Your one-time verification code is: %s\n" +
                        "The code is valid for 10 minutes.\n\n" +
                        "If you did not request a password reset, simply ignore this email and continue using your current password.",
                code
        ));
        mailSender.send(message);
    }
}