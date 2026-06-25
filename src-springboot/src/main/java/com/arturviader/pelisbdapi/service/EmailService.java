package com.arturviader.pelisbdapi.service;

public interface EmailService {
    void send(String to, String subject, String text);
}
