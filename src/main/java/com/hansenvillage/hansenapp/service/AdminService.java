package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.controller.AuthController;
import com.hansenvillage.hansenapp.dto.AdminRegistrationRequest;
import com.hansenvillage.hansenapp.dto.LoginRequest;
import com.hansenvillage.hansenapp.dto.LoginResponse;
import com.hansenvillage.hansenapp.entity.AdminUser;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.AdminUserRepository;
import com.hansenvillage.hansenapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminUserRepository adminUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


//    @Transactional
//    public void registerAdminOrManager(AdminRegistrationRequest request) {
//        if (adminUserRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("User already exists");
//        }
//
//        AdminUser admin = new AdminUser();
//        admin.setEmail(request.getEmail());
//        admin.setPassword(passwordEncoder.encode(request.getPassword()));
//        admin.setRoles(request.getRoles());
//
//        adminUserRepository.save(admin);
//    }

//    @Transactional
//    public LoginResponse loginAdmin(LoginRequest request) {
//        AdminUser admin = adminUserRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));
//
//        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
//            throw FamilyException.of(FamilyErrorCode.WRONG_PASSWORD);
//        }
//
//        String accessToken = jwtService.generateAdminToken(admin);
//        String refreshToken = jwtService.generateAdminRefreshToken(admin);
//
//        LoginResponse response = new LoginResponse();
//        response.setAccessToken(accessToken);
//        response.setRefreshToken(refreshToken);
//        return response;
//    }
}
