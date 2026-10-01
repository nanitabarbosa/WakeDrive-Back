package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordSetupEmail(String to, String resetLink) {
        send(to, "Bienvenido a WakeDrive - Configura tu contraseña",
                "Tu empresa fue aprobada en WakeDrive. Configura tu contraseña en el siguiente enlace: " + resetLink);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        send(to, "WakeDrive - Recuperar contraseña",
                "Recibimos una solicitud para restablecer tu contraseña. Usa este enlace: " + resetLink);
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
