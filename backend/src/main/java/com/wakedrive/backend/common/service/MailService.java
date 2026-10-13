package com.wakedrive.backend.common.service;

public interface MailService {

    void sendAccountCreatedEmail(String to, String adminName, String email, String password);

    void sendPasswordResetEmail(String to, String userName, String resetLink);

    void sendCompanyRejectedEmail(String to, String userName, String companyName, String retryLink);
}
