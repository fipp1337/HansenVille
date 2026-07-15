package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.service.AdminService;
import com.hansenvillage.hansenapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AdminService adminService;


    @PostMapping("/register/initiate")
    public ResponseEntity<RegistrationInitiateResponse> initiateRegistration(@Valid @RequestBody InitiateRegistrationRequest request) {
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

    @PostMapping("/password/reset")
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

//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        adminService.registerAdmin(request);
        return ResponseEntity.ok("Admin registered successfully");
}

    @PostMapping("/admin/login")
    public LoginResponse adminLogin(@Valid @RequestBody LoginRequest request) {
        return adminService.loginAdmin(request);
    }
}
