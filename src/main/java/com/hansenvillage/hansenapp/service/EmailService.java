package com.hansenvillage.hansenapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async("mailExecutor")
    public void sendVerificationEmail(String toEmail, String code) {
        send(
                toEmail,
                "Code registration confirm | Hansen Village",
                """
                        Hi!

                        Your registration confirmation code for Hansen Village Application: %s
                        This code is valid for 10 minutes.

                        If you did not make this request, simply ignore this email.
                        """.formatted(code)
        );
    }

    @Async("mailExecutor")
    public void sendVerificationAdminEmail(String toEmail, String code) {
        send(
                toEmail,
                "Admin code login confirm | Hansen Village",
                """
                        Hi!

                        Your admin login confirmation code for Hansen Village Application: %s
                        This code is valid for 10 minutes.

                        If you did not make this request, simply ignore this email.
                        """.formatted(code)
        );
    }

    @Async("mailExecutor")
    public void sendResetPasswordEmail(String toEmail, String code) {
        send(
                toEmail,
                "Forgot password code | Hansen Village",
                """
                        Hello!

                        You have requested a password reset for your Hansen Village account.
                        Your one-time verification code is: %s
                        The code is valid for 10 minutes.

                        If you did not request a password reset, simply ignore this email and continue using your current password.
                        """.formatted(code)
        );
    }

    private void send(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        log.info("Email sent: to={}, subject={}", toEmail, subject);
    }
}
