package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.FamilyRoleMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.AdminUserRepository;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.FamilyRoleRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.JwtService;
import com.hansenvillage.hansenapp.security.SecurityFamily;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final FamilyRoleRepository familyRoleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final FamilyMapper familyMapper;
    private final FamilyRoleMapper familyRoleMapper;
    private final PhoneService phoneService;


    @Transactional
    public Family registerFamily(FamilyRegistrationRequest request) {
        if (familyRepository.existsByEmail(request.getEmail())) {
            throw FamilyException.of(FamilyErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Family family = familyMapper.toEntity(request);
        family.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            family.setPhoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        int memberCount = request.getMembers() != null ? request.getMembers().size() : 0;
        family.setMemberCount(memberCount);

        Family savedFamily = familyRepository.save(family);

        FamilyRole familyRole = familyRoleMapper.createUserRole(savedFamily.getId());
        familyRoleRepository.save(familyRole);

        List<User> userList = userMapper.toEntityList(request.getMembers());
        userList.forEach(user -> user.setFamilyId(savedFamily.getId()));
        userRepository.saveAll(userList);

        return savedFamily;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Family family = familyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.WRONG_EMAIL));

        if (!passwordEncoder.matches(request.getPassword(), family.getPassword())) {
            throw FamilyException.of(FamilyErrorCode.WRONG_PASSWORD);
        }

        List<Role> roles = familyRoleRepository.findByFamilyId(family.getId()).stream()
                .map(familyRole -> Role.valueOf(familyRole.getRole()))
                .toList();

        String accessToken = jwtService.generateToken(family, roles);
        String refreshToken = jwtService.generateRefreshToken(family, roles);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    @Transactional(readOnly = true)
    public LoginResponse refreshToken(RefreshRequest request) {
        SecurityFamily securityFamily = jwtService.parseRefreshToken(request.getRefreshToken());

        Family family = new Family();
        family.setId(securityFamily.getId());
        family.setEmail(securityFamily.getEmail());
        family.setPassword("");

        List<Role> roles = securityFamily.getRoles();

        String newAccessToken = jwtService.generateToken(family, roles);
        String newRefreshToken = jwtService.generateRefreshToken(family, roles);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);

        return response;
    }
}