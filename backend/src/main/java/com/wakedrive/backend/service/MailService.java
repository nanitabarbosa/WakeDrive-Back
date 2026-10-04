package com.wakedrive.backend.service;

public interface MailService {

    void sendAccountCreatedEmail(String to, String adminName, String email, String password);

    void sendPasswordResetEmail(String to, String resetLink);
}
