package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/initiate")
    public MessageResponse initiateRegistration(
            @Valid @RequestBody RegistrationInitiateRequest request) {
        authService.initiateRegistration(request);
        return new MessageResponse("Registration initiated successfully");
    }

    @PostMapping("/register/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse confirmRegistration(
            @Valid @RequestBody FamilyRegistrationRequest request) {
        return authService.registerFamily(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/password/forgot")
    public MessageResponse forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiateForgotPassword(request);
        return new MessageResponse("Forgot password initiated successfully");
    }

    @PostMapping("/password/reset")
    public MessageResponse resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return new MessageResponse("Password was reset successfully");
    }

    @PostMapping("/admins/login/initiate")
    public MessageResponse adminLoginInitiate(
            @Valid @RequestBody AdminLoginInitiateRequest request) {
        authService.initiateAdminLogin(request);
        return new MessageResponse("Admin login initiated successful");
    }

    @PostMapping("/admins/login/confirm")
    public LoginResponse adminLoginConfirm(
            @Valid @RequestBody AdminLoginConfirmRequest request) {
        return authService.confirmAdminLogin(request);
    }

    @PostMapping("/resend-code")
    public MessageResponse resendCode(
            @Valid @RequestBody ResendCodeRequest request) {
        authService.resendOtp(request.email(), request.type());
        return new MessageResponse("Verification code has been resent");
    }
}