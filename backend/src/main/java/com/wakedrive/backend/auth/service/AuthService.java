package com.wakedrive.backend.auth.service;

import com.wakedrive.backend.auth.dto.AuthResponseDTO;
import com.wakedrive.backend.auth.dto.ChangePasswordRequestDTO;
import com.wakedrive.backend.auth.dto.ForgotPasswordRequestDTO;
import com.wakedrive.backend.auth.dto.LoginRequestDTO;
import com.wakedrive.backend.auth.dto.RefreshTokenRequestDTO;
import com.wakedrive.backend.auth.dto.ResetPasswordRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO request);

    AuthResponseDTO refresh(RefreshTokenRequestDTO request);

    void forgotPassword(ForgotPasswordRequestDTO request);

    void validateResetToken(String token);

    void resetPassword(ResetPasswordRequestDTO request);

    void changePassword(ChangePasswordRequestDTO request);
}
