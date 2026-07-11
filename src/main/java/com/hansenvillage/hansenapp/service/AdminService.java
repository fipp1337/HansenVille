package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AdminRegistrationRequest;
import com.hansenvillage.hansenapp.dto.LoginRequest;
import com.hansenvillage.hansenapp.dto.LoginResponse;
import com.hansenvillage.hansenapp.entity.AdminUser;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.AdminMapper;
import com.hansenvillage.hansenapp.repository.*;
import com.hansenvillage.hansenapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminUserRepository adminUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final FamilyRoleRepository familyRoleRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final CinemaBookingRepository cinemaBookingRepository;
    private final AdminMapper adminMapper;


    @Transactional
    public void registerAdmin(AdminRegistrationRequest request) {
        AdminUser admin = adminMapper.toEntity(request);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminUserRepository.save(admin);
    }

    @Transactional
    public LoginResponse loginAdmin(LoginRequest request) {
        AdminUser admin = adminUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw FamilyException.of(FamilyErrorCode.WRONG_PASSWORD, request.getPassword());
        }

        String accessToken = jwtService.generateAdminToken(admin);
        String refreshToken = jwtService.generateAdminRefreshToken(admin);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public List<Family> findFamiliesByAddress(String address) {
        return familyRepository.findByAddress(address);
    }

    @Transactional
    public void deleteFamilyByAddress(String address) {
        List<Family> families = familyRepository.findByAddress(address);

        if (families.isEmpty()) {
            throw FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND);
        }

        for (Family family : families) {
            UUID familyId = family.getId();

            List<User> familyMembers = userRepository.findByFamilyId(familyId);
            List<UUID> userIds = familyMembers.stream().map(User::getId).toList();

            if (!userIds.isEmpty()) {
                poolBookingRepository.deleteByUserIdIn(userIds);
                cinemaBookingRepository.deleteByUserIdIn(userIds);
            }

            userRepository.deleteByFamilyId(familyId);

            familyRoleRepository.deleteByFamilyId(familyId);

            familyRepository.delete(family);
        }
    }
}
