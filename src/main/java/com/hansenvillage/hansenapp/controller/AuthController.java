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
    public RegistrationInitiateResponse initiateRegistration(
            @Valid @RequestBody RegistrationInitiateRequest request) {
        authService.initiateRegistration(request);
        RegistrationInitiateResponse response = new RegistrationInitiateResponse();
        response.setMessage("Successful initiate registration");
        return response;
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
    public ForgotPasswordInitiateResponse forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiateForgotPassword(request);
        ForgotPasswordInitiateResponse response = new ForgotPasswordInitiateResponse();
        response.setMessage("Successful initiate forgot password");
        return response;
    }

    @PostMapping("/password/reset")
    public ResetPasswordResponse resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        ResetPasswordResponse response = new ResetPasswordResponse();
        response.setMessage("Successful reset password");
        return response;
    }

    @PostMapping("/admin/login/initiate")
    public AdminLoginInitiateResponse adminLoginInitiate(
            @Valid @RequestBody AdminLoginInitiateRequest request) {
        authService.initiateAdminLogin(request);
        AdminLoginInitiateResponse response = new AdminLoginInitiateResponse();
        response.setMessage("Admin Login Initiate successful");
        return response;
    }

    @PostMapping("/admin/login/confirm")
    public LoginResponse adminLoginConfirm(
            @Valid @RequestBody AdminLoginConfirmRequest request) {
        return authService.confirmAdminLogin(request);
    }

    @PostMapping("/resend-code")
    public String resendCode(
            @Valid @RequestBody ResendCodeRequest request) {
        authService.resendOtp(request.email(), request.type());
        return "Verification code has been resent";
    }
}