package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.constant.AppConstant;
import com.hansenvillage.hansenapp.dto.FamilyBookingHistoryResponse;
import com.hansenvillage.hansenapp.dto.FamilyInfoResponse;
import com.hansenvillage.hansenapp.dto.FamilyUpdateRequest;
import com.hansenvillage.hansenapp.dto.PoolTicketsResponse;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.*;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final PhoneService phoneService;
    private final PoolBookingRepository poolBookingRepository;
    private final CinemaBookingRepository cinemaBookingRepository;
    private final FamilyRoleRepository familyRoleRepository;
    private final FamilyMapper familyMapper;
    private final UserMapper userMapper;
    private final PoolBookingService poolBookingService;
    private final CinemaBookingService cinemaBookingService;
    private final FileStorageService fileStorageService;
    private final ImageValidationService imageValidationService;
    private final FamilyPasswordService familyPasswordService;

    public int getFamilySize(UUID familyId) {
        return userRepository.countByFamilyId(familyId);
    }

    public Family findById(UUID id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.FAMILY_NOT_FOUND, id));
    }

    @Transactional
    public Family updateFamilyInfo(UUID id, FamilyUpdateRequest request) {
        SecurityUtils.assertOwnerOrSuperAdmin(id);
        Family family = findById(id);

        familyMapper.updateFamilyFromRequest(request, family);
        applyPasswordUpdateIfRequested(family, request);

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            family.setPhoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        Family saved = familyRepository.save(family);
        log.info("Family updated: {}", id);
        return saved;
    }

    @Transactional
    public Family updateFamilyInfoById(UUID id, FamilyUpdateRequest request) {
        SecurityUtils.assertOwnerOrSuperAdmin(id);
        Family family = findById(id);

        familyMapper.updateFamilyFromRequest(request, family);
        applyPasswordUpdateIfRequested(family, request);

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            family.setPhoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        Family saved = familyRepository.save(family);
        log.info("Family updated: {}", id);
        return saved;
    }

    @Transactional
    public void deleteFamily(UUID id) {
        SecurityUtils.assertOwnerOrSuperAdmin(id);
        deleteFamilyData(id);
    }

    @Transactional
    public void deleteFamilyData(UUID id) {
        List<User> familyMembers = userRepository.findByFamilyId(id);
        List<UUID> userIds = familyMembers.stream().map(User::getId).toList();
        if (!userIds.isEmpty()) {
            poolBookingRepository.deleteByUserIdIn(userIds);
            cinemaBookingRepository.deleteByUserIdIn(userIds);
        }
        userRepository.deleteByFamilyId(id);
        familyRoleRepository.deleteByFamilyId(id);
        familyRepository.deleteById(id);
        log.info("Family deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public FamilyInfoResponse getFamilyInfoById(UUID id) {
        SecurityUtils.assertOwnerOrSuperAdmin(id);
        Family family = findById(id);
        FamilyInfoResponse response = familyMapper.toInfoResponse(family);
        response.setMembers(userMapper.toResponse(userRepository.findByFamilyId(id)));
        return response;
    }

    @Transactional(readOnly = true)
    public PoolTicketsResponse getPoolTickets(LocalDate targetDate) {
        LocalDate date = targetDate != null ? targetDate : LocalDate.now();
        UUID familyId = SecurityUtils.currentFamilyId();
        Family family = findById(familyId);

        long maxTickets = family.getMemberCount() * 2L;
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        long usedTickets = poolBookingService.countBookingsForFamilyInWeek(familyId, monday, sunday);
        long remainingTickets = Math.max(0, maxTickets - usedTickets);
        LocalDateTime resetTime = monday.plusWeeks(1).atStartOfDay();

        return new PoolTicketsResponse(maxTickets, remainingTickets, resetTime);
    }

    @Transactional
    public Family updateFamilyInfoByJwt(FamilyUpdateRequest request) {
        return updateFamilyInfo(SecurityUtils.currentFamilyId(), request);
    }

    @Transactional
    public void deleteFamilyByJwt() {
        deleteFamily(SecurityUtils.currentFamilyId());
    }

    @Transactional(readOnly = true)
    public FamilyInfoResponse getFamilyInfoByJwt() {
        return getFamilyInfoById(SecurityUtils.currentFamilyId());
    }

    @Transactional(readOnly = true)
    public FamilyBookingHistoryResponse getFamilyBookingHistoryByJwt() {
        UUID familyId = SecurityUtils.currentFamilyId();
        return new FamilyBookingHistoryResponse(
                poolBookingService.getPoolBookingHistory(familyId),
                cinemaBookingService.getCinemaBookingHistory(familyId)
        );
    }

    private void applyPasswordUpdateIfRequested(Family family, FamilyUpdateRequest request) {
        if (!hasPasswordUpdate(request)) {
            return;
        }

        familyPasswordService.updatePassword(
                family,
                request.getOldPassword(),
                request.getNewPassword(),
                request.getConfirmNewPassword()
        );
    }

    private boolean hasPasswordUpdate(FamilyUpdateRequest request) {
        return hasText(request.getOldPassword())
                || hasText(request.getNewPassword())
                || hasText(request.getConfirmNewPassword());
    }

    @Transactional
    public void uploadProfilePicture(MultipartFile file) {
        imageValidationService.validate(file);
        UUID familyId = SecurityUtils.currentFamilyId();
        Family family = findById(familyId);
        family.setProfilePicture(fileStorageService.store(file, AppConstant.Upload.FAMILIES_DIR));
        familyRepository.save(family);
        log.info("Profile picture uploaded: family={}", familyId);
    }

    @Transactional(readOnly = true)
    public Resource getProfilePictureById(UUID familyId) {
        Family family = findById(familyId);
        return fileStorageService.loadAsResource(
                AppConstant.Upload.FAMILIES_DIR,
                family.getProfilePicture(),
                AppErrorCode.PROFILE_PICTURE_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public Resource getProfilePicture() {
        UUID familyId = SecurityUtils.currentFamilyId();
        Family family = findById(familyId);
        return fileStorageService.loadAsResource(
                AppConstant.Upload.FAMILIES_DIR,
                family.getProfilePicture(),
                AppErrorCode.PROFILE_PICTURE_NOT_FOUND);
    }

    @Transactional
    public void updateProfilePicture(MultipartFile newFile) {
        deleteProfilePicture();
        uploadProfilePicture(newFile);
    }

    @Transactional
    public void deleteProfilePicture() {
        UUID familyId = SecurityUtils.currentFamilyId();
        Family family = findById(familyId);
        if (family.getProfilePicture() == null) {
            throw AppException.of(AppErrorCode.PROFILE_PICTURE_NOT_FOUND, familyId);
        }

        fileStorageService.delete(AppConstant.Upload.FAMILIES_DIR, family.getProfilePicture());
        family.setProfilePicture(null);
        familyRepository.save(family);
    }
}
