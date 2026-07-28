package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.AdminLoginConfirmRequest;
import com.hansenvillage.hansenapp.dto.AdminLoginInitiateRequest;
import com.hansenvillage.hansenapp.dto.AdminLoginInitiateResponse;
import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.dto.ForgotPasswordInitiateResponse;
import com.hansenvillage.hansenapp.dto.ForgotPasswordRequest;
import com.hansenvillage.hansenapp.dto.LoginRequest;
import com.hansenvillage.hansenapp.dto.LoginResponse;
import com.hansenvillage.hansenapp.dto.RefreshRequest;
import com.hansenvillage.hansenapp.dto.RegistrationInitiateRequest;
import com.hansenvillage.hansenapp.dto.RegistrationInitiateResponse;
import com.hansenvillage.hansenapp.dto.ResetPasswordRequest;
import com.hansenvillage.hansenapp.dto.ResetPasswordResponse;
import com.hansenvillage.hansenapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RegistrationInitiateResponse> initiateRegistration(
            @Valid @RequestBody RegistrationInitiateRequest request) {
        authService.initiateRegistration(request);
        RegistrationInitiateResponse response = new RegistrationInitiateResponse();
        response.setMessage("Successful initiate registration");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ForgotPasswordInitiateResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiateForgotPassword(request);
        ForgotPasswordInitiateResponse response = new ForgotPasswordInitiateResponse();
        response.setMessage("Successful initiate forgot password");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        ResetPasswordResponse response = new ResetPasswordResponse();
        response.setMessage("Successful reset password");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginResponse> confirmRegistration(
            @Valid @RequestBody FamilyRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerFamily(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/admin/login/initiate")
    public ResponseEntity<AdminLoginInitiateResponse> adminLoginInitiate(
            @Valid @RequestBody AdminLoginInitiateRequest request) {
        authService.initiateAdminLogin(request);
        AdminLoginInitiateResponse response = new AdminLoginInitiateResponse();
        response.setMessage("Admin Login Initiate successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/login/confirm")
    public ResponseEntity<LoginResponse> adminLoginConfirm(
            @Valid @RequestBody AdminLoginConfirmRequest request) {
        return ResponseEntity.ok(authService.confirmAdminLogin(request));
    }
}
