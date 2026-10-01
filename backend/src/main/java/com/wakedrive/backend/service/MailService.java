package com.wakedrive.backend.service;

public interface MailService {

    void sendPasswordSetupEmail(String to, String resetLink);

    void sendPasswordResetEmail(String to, String resetLink);
}
