package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final FamilyMapper familyMapper;

    public AuthController(AuthService authService, FamilyMapper familyMapper) {
        this.authService = authService;
        this.familyMapper = familyMapper;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyRegistrationResponse register(@Valid @RequestBody FamilyRegistrationRequest request) {
        Family savedFamily = authService.registerFamily(request);
        return familyMapper.toResponse(savedFamily);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request);
    }
}
