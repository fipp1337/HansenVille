package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.repository.AdminUserRepository;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.InviteCodeRepository;
import com.hansenvillage.hansenapp.security.JwtService;
import com.hansenvillage.hansenapp.security.SecurityFamily;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int CODE_LENGTH = 6;
    private static final int CODE_BOUND = 1_000_000;

    private final FamilyRepository familyRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final FamilyMapper familyMapper;
    private final PhoneService phoneService;
    private final RedisService redisService;
    private final InviteCodeRepository inviteCodeRepository;
    private final EmailService emailService;
    private final AdminUserRepository adminUserRepository;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void initiateRegistration(RegistrationInitiateRequest request) {
        String email = normalizeEmail(request.getEmail());
        String address = request.getAddress();

        if (familyRepository.existsByEmail(email)) {
            throw AppException.of(AppErrorCode.EMAIL_ALREADY_EXISTS, email);
        }

        if (familyRepository.existsByAddress(address)) {
            throw AppException.of(AppErrorCode.ADDRESS_ALREADY_EXISTS, address);
        }

        InviteCode inviteCode = inviteCodeRepository
                .findByCodeAndStatus(request.getInviteCode(), InviteCodeStatus.AVAILABLE)
                .orElseThrow(() -> AppException.of(AppErrorCode.INVALID_INVITE_CODE));

        String verificationCode = generateVerificationCode();
        redisService.storeRegistrationData(email, inviteCode.getCode(), verificationCode, address);
        emailService.sendVerificationEmail(email, verificationCode);
        log.info("Registration initiated: {}", email);
    }

    @Transactional
    public LoginResponse registerFamily(FamilyRegistrationRequest request) {
        String email = normalizeEmail(request.getEmail());

        Map<Object, Object> registrationData = redisService.getRegistrationData(email);
        if (registrationData == null || registrationData.isEmpty()) {
            throw AppException.of(AppErrorCode.REGISTRATION_NOT_INITIATED, email);
        }

        String storedVerificationCode = (String) registrationData.get("verificationCode");
        if (storedVerificationCode == null || storedVerificationCode.isBlank()
                || !storedVerificationCode.equals(request.getVerificationCode())) {
            throw AppException.of(AppErrorCode.INVALID_VERIFICATION_CODE);
        }

        String storedInviteCode = (String) registrationData.get("inviteCode");
        String storedAddress = (String) registrationData.get("address");

        InviteCode inviteCode = inviteCodeRepository
                .findByCodeAndStatus(storedInviteCode, InviteCodeStatus.AVAILABLE)
                .orElseThrow(() -> AppException.of(AppErrorCode.INVALID_INVITE_CODE));

        if (familyRepository.existsByEmail(email)) {
            throw AppException.of(AppErrorCode.EMAIL_ALREADY_EXISTS, email);
        }
        if (familyRepository.existsByAddress(storedAddress)) {
            throw AppException.of(AppErrorCode.ADDRESS_ALREADY_EXISTS, storedAddress);
        }

        inviteCode.setStatus(InviteCodeStatus.USED);
        inviteCode.setEmail(email);
        inviteCodeRepository.save(inviteCode);

        Family.FamilyBuilder familyBuilder = Family.builder()
                .email(email)
                .address(storedAddress)
                .password(passwordEncoder.encode(storedInviteCode));

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            familyBuilder.phoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        Family family = familyBuilder.build();
        family.getRoles().add(Role.USER);
        Family savedFamily = familyRepository.save(family);

        redisService.deleteRegistrationData(email);

        List<Role> roles = new ArrayList<>(savedFamily.getRoles());
        log.info("Family registered: id={}, email={}", savedFamily.getId(), email);

        return buildLoginResponse(
                jwtService.generateToken(savedFamily, roles),
                jwtService.generateRefreshToken(savedFamily, roles)
        );
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> AppException.of(AppErrorCode.WRONG_EMAIL, email));

        if (!passwordEncoder.matches(request.getPassword(), family.getPassword())) {
            throw AppException.of(AppErrorCode.WRONG_PASSWORD, request.getPassword());
        }

        List<Role> roles = new ArrayList<>(family.getRoles());

        log.info("Family login: id={}, email={}", family.getId(), email);
        return buildLoginResponse(
                jwtService.generateToken(family, roles),
                jwtService.generateRefreshToken(family, roles)
        );
    }

    @Transactional(readOnly = true)
    public LoginResponse refreshToken(RefreshRequest request) {
        SecurityFamily securityFamily = jwtService.parseRefreshToken(request.getRefreshToken());

        return buildLoginResponse(
                jwtService.generateToken(securityFamily),
                jwtService.generateRefreshToken(securityFamily)
        );
    }

    public void initiateForgotPassword(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());

        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> AppException.of(AppErrorCode.WRONG_EMAIL, email));

        String resetCode = generateVerificationCode();
        redisService.storeResetCode(family.getEmail(), resetCode);
        emailService.sendResetPasswordEmail(family.getEmail(), resetCode);
        log.info("Password reset initiated: {}", email);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw AppException.of(AppErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        String email = normalizeEmail(request.getEmail());
        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> AppException.of(AppErrorCode.WRONG_EMAIL, email));

        String storedCode = redisService.getResetCode(email);
        if (storedCode == null || !storedCode.equals(request.getVerificationCode())) {
            throw AppException.of(AppErrorCode.INVALID_VERIFICATION_CODE);
        }

        family.setPassword(passwordEncoder.encode(request.getNewPassword()));
        familyRepository.save(family);
        redisService.deleteResetCode(email);
        log.info("Password reset completed: {}", email);
    }

    @Transactional
    public void initiateAdminLogin(AdminLoginInitiateRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (!adminUserRepository.existsByEmailIgnoreCase(email)) {
            throw AppException.of(AppErrorCode.USER_NOT_FOUND, email);
        }

        String verificationCode = generateVerificationCode();
        redisService.storeLoginAdminData(email, verificationCode);
        emailService.sendVerificationAdminEmail(email, verificationCode);
        log.info("Admin login initiated: {}", email);
    }

    @Transactional
    public LoginResponse confirmAdminLogin(AdminLoginConfirmRequest request) {
        String email = normalizeEmail(request.getEmail());

        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, email));

        String storedCode = redisService.getLoginAdminCode(email);
        if (storedCode == null || storedCode.isBlank() || !storedCode.equals(request.getVerificationCode())) {
            throw AppException.of(AppErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisService.deleteLoginAdminData(email);
        log.info("Admin login confirmed: id={}, email={}", admin.getId(), email);
        return buildLoginResponse(
                jwtService.generateAdminToken(admin),
                jwtService.generateAdminRefreshToken(admin)
        );
    }

    private LoginResponse buildLoginResponse(String accessToken, String refreshToken) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String generateVerificationCode() {
        return String.format("%0" + CODE_LENGTH + "d", random.nextInt(CODE_BOUND));
    }

    @Transactional
    public void registrationFamilyByAdmin(FamilyAdminRegistrationRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (familyRepository.existsByEmail(email)) {
            throw AppException.of(AppErrorCode.EMAIL_ALREADY_EXISTS, email);
        }
        if (familyRepository.existsByAddress(request.getAddress())) {
            throw AppException.of(AppErrorCode.ADDRESS_ALREADY_EXISTS, request.getAddress());
        }

        Family family = familyMapper.toEntity(request);
        family.setEmail(email);
        family.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            family.setPhoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        family.getRoles().add(Role.USER);
        Family savedFamily = familyRepository.save(family);

        log.info("Family registered by admin: id={}, email={}", savedFamily.getId(), email);
    }

    public void resendOtp(String email, OtpType type) {
        String normalizedEmail = normalizeEmail(email);
        String actionType = type.name();

        if (!redisService.isResendAllowed(normalizedEmail, actionType)) {
            long remainingSeconds = redisService.getRemainingCooldown(normalizedEmail, actionType);
            throw AppException.of(AppErrorCode.OTP_TOO_MANY_REQUESTS, remainingSeconds);
        }

        String newCode = generateVerificationCode();

        switch (type) {
            case REGISTRATION -> {
                redisService.updateRegistrationVerificationCode(normalizedEmail, newCode);
                emailService.sendVerificationEmail(normalizedEmail, newCode);
            }
            case ADMIN_LOGIN -> {
                redisService.updateAdminLoginCode(normalizedEmail, newCode);
                emailService.sendVerificationAdminEmail(normalizedEmail, newCode);
            }
            case RESET_PASSWORD -> {
                redisService.updateResetCode(normalizedEmail, newCode);
                emailService.sendResetPasswordEmail(normalizedEmail, newCode);
            }
        }
        redisService.setResendCooldown(normalizedEmail, actionType);

        log.info("OTP resent successfully for email: {}, type: {}", normalizedEmail, type);
    }
}
