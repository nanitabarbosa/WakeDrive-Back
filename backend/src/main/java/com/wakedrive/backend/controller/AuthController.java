package com.wakedrive.backend.controller;

import com.wakedrive.backend.dto.AuthResponseDTO;
import com.wakedrive.backend.dto.ChangePasswordRequestDTO;
import com.wakedrive.backend.dto.ForgotPasswordRequestDTO;
import com.wakedrive.backend.dto.LoginRequestDTO;
import com.wakedrive.backend.dto.ResetPasswordRequestDTO;
import com.wakedrive.backend.service.AuthService;
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
