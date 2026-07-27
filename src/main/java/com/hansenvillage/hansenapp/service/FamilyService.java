package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.*;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyService {
   private final FamilyRepository familyRepository;
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final PhoneService phoneService;
    private final PoolBookingRepository poolBookingRepository;
    private final CinemaBookingRepository cinemaBookingRepository;
    private final FamilyRoleRepository familyRoleRepository;
    private final FamilyMapper familyMapper;
    private final UserMapper userMapper;
    private final PoolBookingService poolBookingService;
    private final CinemaBookingService cinemaBookingService;

    public int getFamilySize(UUID familyId) {
        return userRepository.countByFamilyId(familyId);
    }

    public Family findById(UUID id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id));
    }


    @Transactional
    public Family updateFamilyInfo(UUID id, FamilyUpdateRequest request) {
        SecurityUtils.assertOwnerOrSuperAdmin(id);
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id));

        familyMapper.updateFamilyFromRequest(request, family);

        boolean isPasswordUpdateAttempt =
                (request.getOldPassword() != null && !request.getOldPassword().isBlank()) ||
                        (request.getNewPassword() != null && !request.getNewPassword().isBlank()) ||
                        (request.getConfirmNewPassword() != null && !request.getConfirmNewPassword().isBlank());

        if (isPasswordUpdateAttempt) {

            if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
                throw FamilyException.of(FamilyErrorCode.OLD_PASSWORD_REQUIRED);
            }

            if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
                throw FamilyException.of(FamilyErrorCode.NEW_PASSWORD_REQUIRED);
            }

            if (request.getNewPassword().equals(request.getOldPassword())) {
                throw FamilyException.of(FamilyErrorCode.NEW_PASSWORD_MATCH_WITH_OLD);
            }

            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw FamilyException.of(FamilyErrorCode.PASSWORDS_DO_NOT_MATCH);
            }

            if (!passwordEncoder.matches(request.getOldPassword(), family.getPassword())) {
                throw FamilyException.of(FamilyErrorCode.INVALID_OLD_PASSWORD);
            }

            family.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            family.setPhoneNumber(phoneService.validateAndFormatPhone(request.getPhoneNumber()));
        }

        return familyRepository.save(family);
    }

    @Transactional
    public void deleteFamily(UUID id) {
        SecurityUtils.assertOwnerOrSuperAdmin(id);
        List<User> familyMembers = userRepository.findByFamilyId(id);
        List<UUID> userIds = familyMembers.stream().map(User::getId).toList();
        if (!userIds.isEmpty()) {
            poolBookingRepository.deleteByUserIdIn(userIds);
            cinemaBookingRepository.deleteByUserIdIn(userIds);
        }
        userRepository.deleteByFamilyId(id);
        familyRoleRepository.deleteByFamilyId(id);
        familyRepository.deleteById(id);
    }

    @Transactional
    public FamilyInfoResponse getFamilyInfoById(UUID id) {
        SecurityUtils.assertOwnerOrSuperAdmin(id);
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id));
        List<User> members = userRepository.findByFamilyId(id);
        FamilyInfoResponse response = familyMapper.toInfoResponse(family);
        response.setMembers(userMapper.toResponse(members));

        return response;
    }

    public PoolTicketsResponse getFamilyTickets(LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        UUID familyId = SecurityUtils.currentFamilyId();
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, familyId));

        long memberCount = family.getMemberCount();
        long maxTickets = memberCount * 2;

        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));


        long usedTickets = poolBookingService.countBookingsForFamilyInWeek(familyId, monday, sunday);
        long remainingTickets = Math.max(0, maxTickets - usedTickets);

        LocalDateTime resetTime = monday.plusWeeks(1).atStartOfDay();

        return new PoolTicketsResponse(maxTickets, remainingTickets, resetTime);
    }

    @Transactional
    public Family updateFamilyInfoByJwt(FamilyUpdateRequest request) {
        UUID familyId = SecurityUtils.currentFamilyId();
        return updateFamilyInfo(familyId, request);
    }

    @Transactional
    public void deleteFamilyByJwt() {
        UUID familyId = SecurityUtils.currentFamilyId();
        deleteFamily(familyId);
    }

    @Transactional
    public FamilyInfoResponse getFamilyInfoByJwt() {
        UUID familyId = SecurityUtils.currentFamilyId();
        return getFamilyInfoById(familyId);
    }

    public FamilyBookingHistoryResponse getFamilyBookingHistoryByJwt() {
        UUID familyId = SecurityUtils.currentFamilyId();

        List<PoolBookingResponse> poolHistory = poolBookingService.getPoolBookingHistory(familyId);
        List<CinemaBookingResponse> cinemaHistory = cinemaBookingService.getCinemaBookingHistory(familyId);
        return new FamilyBookingHistoryResponse(poolHistory, cinemaHistory);
    }
}
