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

    private final FamilyRepository familyRepository;
    private final FamilyRoleRepository familyRoleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
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
        String email = request.getEmail().trim().toLowerCase();

        if (familyRepository.existsByEmail(email)) {
            throw FamilyException.of(FamilyErrorCode.EMAIL_ALREADY_EXISTS, email);
        }
        InviteCode inviteCode = inviteCodeRepository.findByCodeAndStatus(request.getInviteCode(), InviteCodeStatus.AVAILABLE)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.INVALID_INVITE_CODE));
        String verificationCode = generateCode();

        redisService.storeRegistrationData(email, inviteCode.getCode(), verificationCode);
        emailService.sendVerificationEmail(email, verificationCode);
    }

    @Transactional
    public LoginResponse registerFamily(FamilyRegistrationRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw FamilyException.of(FamilyErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        String email = request.getEmail().trim().toLowerCase();

        Map<Object, Object> regData = redisService.getRegistrationData(email);
        if (regData == null || regData.isEmpty()) {
            throw FamilyException.of(FamilyErrorCode.REGISTRATION_NOT_INITIATED, email);
        }

        String storedVerificationCode = (String) regData.get("verificationCode");
        if (storedVerificationCode == null || storedVerificationCode.isBlank()
                || !storedVerificationCode.equals(request.getVerificationCode())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_VERIFICATION_CODE);
        }

        String storedInviteCode = (String) regData.get("inviteCode");
        InviteCode inviteCode = inviteCodeRepository.findByCodeAndStatus(storedInviteCode, InviteCodeStatus.AVAILABLE)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.INVALID_INVITE_CODE));

        if (familyRepository.existsByEmail(email)) {
            throw FamilyException.of(FamilyErrorCode.EMAIL_ALREADY_EXISTS, email);
        }
        if (familyRepository.existsByAddress(request.getAddress())) {
            throw FamilyException.of(FamilyErrorCode.ADDRESS_ALREADY_EXISTS, request.getAddress());
        }

        inviteCode.setStatus(InviteCodeStatus.USED);
        inviteCode.setEmail(email);
        inviteCodeRepository.save(inviteCode);

        Family family = familyMapper.toEntity(request);
        family.setEmail(email);
        family.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            family.setPhoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        Family savedFamily = familyRepository.save(family);

        FamilyRole familyRole = familyRoleMapper.createUserRole(savedFamily.getId());
        familyRoleRepository.save(familyRole);

        redisService.deleteRegistrationData(email);

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
        String email = request.getEmail().trim().toLowerCase();

        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.WRONG_EMAIL, email));

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
        family.setEmail(securityFamily.getEmail().trim().toLowerCase());
        family.setPassword("");

        List<Role> roles = securityFamily.getRoles();

        String newAccessToken = jwtService.generateToken(family, roles);
        String newRefreshToken = jwtService.generateRefreshToken(family, roles);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);

        return response;
    }

    public void initiateForgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.WRONG_EMAIL, email));

        String resetCode = generateCode();
        redisService.storeResetCode(family.getEmail(), resetCode);
        emailService.sendResetPasswordEmail(family.getEmail(), resetCode);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw FamilyException.of(FamilyErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        String email = request.getEmail().trim().toLowerCase();

        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.WRONG_EMAIL, email));

        String storedCode = redisService.getResetCode(email);
        if (storedCode == null || !storedCode.equals(request.getVerificationCode())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_VERIFICATION_CODE);
        }
        family.setPassword(passwordEncoder.encode(request.getNewPassword()));
        familyRepository.save(family);
        redisService.deleteResetCode(email);
    }


    private final SecureRandom random = new SecureRandom();
    private String generateCode() {
        return String.format("%0" + CODE_LENGTH + "d", random.nextInt(CODE_MAX_VALUE));
    }
}