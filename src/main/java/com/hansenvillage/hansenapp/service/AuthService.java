package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.FamilyRoleMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.FamilyRoleRepository;
import com.hansenvillage.hansenapp.repository.InviteCodeRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.JwtService;
import com.hansenvillage.hansenapp.security.SecurityFamily;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

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
    private final RedisService redisService;
    private final InviteCodeRepository inviteCodeRepository;
    private final EmailService emailService;

    private static final int CODE_LENGTH = 6;
    private static final int CODE_MAX_VALUE = 1000000;

    @Transactional
    public void initiateRegistration(InitiateRegistrationRequest request) {
        if (familyRepository.existsByEmail(request.getEmail())) {
            throw FamilyException.of(FamilyErrorCode.EMAIL_ALREADY_EXISTS, request.getEmail());
        }
        InviteCode inviteCode = inviteCodeRepository.findByCodeAndStatus(request.getInviteCode(), InviteCodeStatus.AVAILABLE)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.INVALID_INVITE_CODE));
        String verificationCode = generateCode();
        redisService.storeRegistrationData(request.getEmail(), inviteCode.getCode(), verificationCode);
        emailService.sendVerificationEmail(request.getEmail(), verificationCode);
    }

    @Transactional
    public LoginResponse registerFamily(FamilyRegistrationRequest request) {
        Map<Object, Object> regData = redisService.getRegistrationData(request.getEmail());
        if (regData == null || regData.isEmpty()) {
            throw FamilyException.of(FamilyErrorCode.REGISTRATION_NOT_INITIATED, request.getEmail());
        }

        String storedVerificationCode = (String) regData.get("verificationCode");
        if (storedVerificationCode == null || storedVerificationCode.isBlank()
                || !storedVerificationCode.equals(request.getVerificationCode())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_VERIFICATION_CODE);
        }

        String storedInviteCode = (String) regData.get("inviteCode");
        InviteCode inviteCode = inviteCodeRepository.findByCodeAndStatus(storedInviteCode, InviteCodeStatus.AVAILABLE)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.INVALID_INVITE_CODE));

        if (familyRepository.existsByEmail(request.getEmail())) {
            throw FamilyException.of(FamilyErrorCode.EMAIL_ALREADY_EXISTS, request.getEmail());
        }
        if (familyRepository.existsByAddress(request.getAddress())) {
            throw FamilyException.of(FamilyErrorCode.ADDRESS_ALREADY_EXISTS, request.getAddress());
        }

        inviteCode.setStatus(InviteCodeStatus.USED);
        inviteCode.setEmail(request.getEmail());
        inviteCodeRepository.save(inviteCode);

        Family family = familyMapper.toEntity(request);
        family.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            family.setPhoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        Family savedFamily = familyRepository.save(family);

        FamilyRole familyRole = familyRoleMapper.createUserRole(savedFamily.getId());
        familyRoleRepository.save(familyRole);

        redisService.deleteRegistrationData(request.getEmail());

        List<Role> roles = List.of(Role.valueOf(familyRole.getRole()));
        String accessToken = jwtService.generateToken(savedFamily, roles);
        String refreshToken = jwtService.generateRefreshToken(savedFamily, roles);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Family family = familyRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.WRONG_EMAIL, request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), family.getPassword())) {
            throw FamilyException.of(FamilyErrorCode.WRONG_PASSWORD, request.getPassword());
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


    private final SecureRandom random = new SecureRandom();
    private String generateCode() {
        return String.format("%0" + CODE_LENGTH + "d", random.nextInt(CODE_MAX_VALUE));
    }
}