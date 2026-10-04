package com.wakedrive.backend.service;

import com.wakedrive.backend.dto.AuthResponseDTO;
import com.wakedrive.backend.dto.ChangePasswordRequestDTO;
import com.wakedrive.backend.dto.ForgotPasswordRequestDTO;
import com.wakedrive.backend.dto.LoginRequestDTO;
import com.wakedrive.backend.dto.ResetPasswordRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO request);

    void forgotPassword(ForgotPasswordRequestDTO request);

    void validateResetToken(String token);

    void resetPassword(ResetPasswordRequestDTO request);

    void changePassword(ChangePasswordRequestDTO request);
}
