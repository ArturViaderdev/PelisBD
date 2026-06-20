package com.arturviader.pelisbdapi.service;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class EmailServiceForTests implements EmailService {
    private String lastText;
    private final JavaMailSender mailSender;

    public EmailServiceForTests(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String text) {
        this.lastText = text;
    }

    public String getLastText() {
        return lastText;
    }

    public void clear() {
        this.lastText = null;
    }
}
