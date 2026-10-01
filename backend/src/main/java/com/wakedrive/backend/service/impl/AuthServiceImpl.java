package com.wakedrive.backend.service.impl;

import com.wakedrive.backend.dto.AuthResponseDTO;
import com.wakedrive.backend.dto.ForgotPasswordRequestDTO;
import com.wakedrive.backend.dto.LoginRequestDTO;
import com.wakedrive.backend.dto.ResetPasswordRequestDTO;
import com.wakedrive.backend.dto.SessionUserDTO;
import com.wakedrive.backend.entity.User;
import com.wakedrive.backend.exception.ResourceNotFoundException;
import com.wakedrive.backend.repository.UserRepository;
import com.wakedrive.backend.security.JwtService;
import com.wakedrive.backend.service.AuthService;
import com.wakedrive.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getEmail()));

        String token = jwtService.generateToken(user);
        String roleName = user.getRole().getName();

        return AuthResponseDTO.builder()
                .token(token)
                .roles(List.of(roleName))
                .user(SessionUserDTO.builder().name(user.getName()).role(roleName).build())
                .build();
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(2));
            userRepository.save(user);
            mailService.sendPasswordResetEmail(user.getEmail(), buildResetLink(token));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public void validateResetToken(String token) {
        findValidResetToken(token);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO request) {
        User user = findValidResetToken(request.getToken());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    private User findValidResetToken(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token"));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Invalid or expired token");
        }
        return user;
    }

    private String buildResetLink(String token) {
        return frontendUrl + "/auth/reset-password?token=" + token;
    }
}
