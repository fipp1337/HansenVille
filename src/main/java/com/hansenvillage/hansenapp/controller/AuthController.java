package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.service.AdminService;
import com.hansenvillage.hansenapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AdminService adminService;


    @PostMapping("/register/initiate")
    public ResponseEntity<RegistrationInitiateResponse> initiateRegistration(@Valid @RequestBody RegistrationInitiateRequest request) {
        authService.initiateRegistration(request);
        RegistrationInitiateResponse response = new RegistrationInitiateResponse();
        response.setMessage("Successful initiate registration");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ForgotPasswordInitiateResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiateForgotPassword(request);
        ForgotPasswordInitiateResponse response = new ForgotPasswordInitiateResponse();
        response.setMessage("Successful initiate forgot password");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset/test")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        ResetPasswordResponse response = new ResetPasswordResponse();
        response.setMessage("Successful reset password");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginResponse> confirmRegistration(@Valid @RequestBody FamilyRegistrationRequest request) {
        LoginResponse response = authService.registerFamily(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<AdminLoginInitiateResponse> adminLoginInitiate(@Valid @RequestBody AdminLoginInitiateRequest request) {
        authService.loginAdminInitiate(request);
        AdminLoginInitiateResponse response = new AdminLoginInitiateResponse();
        response.setMessage("Admin Login Initiate successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/login/confirm")
    public ResponseEntity<LoginResponse> adminLoginConfirm(@Valid @RequestBody AdminLoginConfirmRequest request) {
        return ResponseEntity.ok(authService.loginAdminConfirm(request));
    }
}
