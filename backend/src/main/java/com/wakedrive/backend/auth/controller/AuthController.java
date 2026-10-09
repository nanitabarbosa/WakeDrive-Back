package com.wakedrive.backend.auth.controller;

import com.wakedrive.backend.auth.dto.AuthResponseDTO;
import com.wakedrive.backend.auth.dto.ChangePasswordRequestDTO;
import com.wakedrive.backend.auth.dto.ForgotPasswordRequestDTO;
import com.wakedrive.backend.auth.dto.LoginRequestDTO;
import com.wakedrive.backend.auth.dto.RefreshTokenRequestDTO;
import com.wakedrive.backend.auth.dto.ResetPasswordRequestDTO;
import com.wakedrive.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return authService.refresh(request);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
    }

    @GetMapping("/reset-password/validate")
    public void validateResetToken(@RequestParam String token) {
        authService.validateResetToken(token);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
    }

    @PatchMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
    }
}
