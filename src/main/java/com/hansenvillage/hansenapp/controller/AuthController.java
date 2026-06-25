package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.dto.FamilyResponse;
import com.hansenvillage.hansenapp.dto.LoginRequest;
import com.hansenvillage.hansenapp.dto.LoginResponse;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.service.AuthService;
import com.hansenvillage.hansenapp.service.FamilyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final FamilyService familyService;
    private final FamilyMapper familyMapper;

    public AuthController(AuthService authService, FamilyService familyService, FamilyMapper familyMapper) {
        this.authService = authService;
        this.familyService = familyService;
        this.familyMapper = familyMapper;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyResponse register(@Valid @RequestBody FamilyRegistrationRequest request) {
        Family savedFamily = authService.registerFamily(request);

        return familyMapper.toResponse(savedFamily);

    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
