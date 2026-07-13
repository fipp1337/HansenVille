package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.FamilyUpdateRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.*;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public int getFamilySize(UUID familyId) {
        return userRepository.countByFamilyId(familyId);
    }

    public Family findById(UUID id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id));
    }

    @Transactional
    public Family updateFamilyInfo(UUID id, FamilyUpdateRequest request) {
        SecurityUtils.assertOwner(id);

        Family family = familyRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id));

        if (request.getEmail() != null && !request.getEmail().isBlank()) family.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) family.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getAddress() != null && !request.getAddress().isBlank()) family.setAddress(request.getAddress());
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            String formattedPhone = phoneService.validateAndFormatPhone(request.getPhoneNumber());
            family.setPhoneNumber(formattedPhone);
        }

        return familyRepository.save(family);
    }

    @Transactional
    public void deleteFamily(UUID id) {
        SecurityUtils.assertOwner(id);
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
}
