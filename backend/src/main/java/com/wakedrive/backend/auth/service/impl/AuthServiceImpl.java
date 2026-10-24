package com.wakedrive.backend.auth.service.impl;

import com.wakedrive.backend.common.util.TextNormalizer;
import com.wakedrive.backend.auth.dto.AuthResponseDTO;
import com.wakedrive.backend.auth.dto.ChangePasswordRequestDTO;
import com.wakedrive.backend.auth.dto.ForgotPasswordRequestDTO;
import com.wakedrive.backend.auth.dto.LoginRequestDTO;
import com.wakedrive.backend.auth.dto.RefreshTokenRequestDTO;
import com.wakedrive.backend.auth.dto.ResetPasswordRequestDTO;
import com.wakedrive.backend.auth.dto.SessionUserDTO;
import com.wakedrive.backend.auth.entity.RefreshToken;
import com.wakedrive.backend.auth.repository.RefreshTokenRepository;
import com.wakedrive.backend.auth.service.AuthService;
import com.wakedrive.backend.common.exception.ResourceNotFoundException;
import com.wakedrive.backend.common.exception.TooManyAttemptsException;
import com.wakedrive.backend.common.service.MailService;
import com.wakedrive.backend.security.CurrentUserProvider;
import com.wakedrive.backend.security.JwtService;
import com.wakedrive.backend.security.LoginAttemptService;
import com.wakedrive.backend.user.entity.User;
import com.wakedrive.backend.user.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final CurrentUserProvider currentUserProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptService loginAttemptService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = TextNormalizer.email(request.getEmail());
        if (loginAttemptService.isBlocked(email)) {
            throw new TooManyAttemptsException("Demasiados intentos fallidos. Inténtalo de nuevo en 15 minutos");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (AuthenticationException e) {
            loginAttemptService.recordFailure(email);
            throw e;
        }
        loginAttemptService.reset(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponseDTO refresh(RefreshTokenRequestDTO request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("La sesión no es válida. Inicia sesión de nuevo"));
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("La sesión expiró. Inicia sesión de nuevo");
        }

        User user = stored.getUser();
        refreshTokenRepository.delete(stored);
        return buildAuthResponse(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        userRepository.findByEmail(TextNormalizer.email(request.getEmail())).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(2));
            userRepository.save(user);
            mailService.sendPasswordResetEmail(user.getEmail(), user.getName(), buildResetLink(token));
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
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {
        User user = currentUserProvider.getCurrentUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
        refreshTokenRepository.deleteByUser(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        if (!user.hasActiveCompany()) {
            throw new DisabledException("La empresa está inactiva");
        }
        String roleName = user.getRole().getName();

        RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs)))
                .build());

        return AuthResponseDTO.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(refreshToken.getToken())
                .roles(List.of(roleName))
                .user(SessionUserDTO.builder()
                        .name(user.getName())
                        .role(roleName)
                        .mustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()))
                        .build())
                .build();
    }

    private User findValidResetToken(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("El enlace es inválido o ha expirado"));
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("El enlace es inválido o ha expirado");
        }
        return user;
    }

    private String buildResetLink(String token) {
        return frontendUrl + "/auth/reset-password?token=" + token;
    }
}
