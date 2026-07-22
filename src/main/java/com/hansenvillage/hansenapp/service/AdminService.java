package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.AdminMapper;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminUserRepository adminUserRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final FamilyRoleRepository familyRoleRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final CinemaBookingRepository cinemaBookingRepository;
    private final AdminMapper adminMapper;
    private final FamilyMapper familyMapper;
    private final UserMapper userMapper;


    @Transactional
    public void addAdmin(AdminRegistrationRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (email.isBlank()) {
            throw FamilyException.of(FamilyErrorCode.WRONG_EMAIL);
        }
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw FamilyException.of(FamilyErrorCode.ROLES_EMPTY);
        }
        if (adminUserRepository.existsByEmail(email)) {
            throw FamilyException.of(FamilyErrorCode.EMAIL_ALREADY_EXISTS, email);
        }

        AdminUser admin = adminMapper.toEntity(request);
        admin.setEmail(email);
        adminUserRepository.save(admin);
    }

    @Transactional
    public AdminUpdateResponse updateAdmin(UUID adminId, AdminUpdateRequest request) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, adminId));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();

            if (!newEmail.equals(admin.getEmail())) {
                if (adminUserRepository.existsByEmail(newEmail)) {
                    throw FamilyException.of(FamilyErrorCode.EMAIL_ALREADY_EXISTS, newEmail);
                }
                admin.setEmail(newEmail);
            }
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            admin.setName(request.getName());
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            admin.setRoles(request.getRoles());
        }

        AdminUser savedAdmin = adminUserRepository.save(admin);
        AdminUpdateResponse response = new AdminUpdateResponse();
        response.setName(savedAdmin.getName());
        response.setEmail(savedAdmin.getEmail());
        response.setRoles(savedAdmin.getRoles());

        return response;
    }
    @Transactional
    public void deleteAdmin(UUID adminId) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));

        adminUserRepository.delete(admin);
    }



    public List<FamilyInfoResponse> findFamiliesByAddress(String address) {
        return familyRepository.findByAddress(address).stream()
                .map(family -> {
                    FamilyInfoResponse response = familyMapper.toInfoResponse(family);
                    response.setMembers(userMapper.toResponse(userRepository.findByFamilyId(family.getId())));
                    return response;
                })
                .toList();
    }

    @Transactional
    public void deleteFamilyByAddress(String address) {
        List<Family> families = familyRepository.findByAddress(address);

        if (families.isEmpty()) {
            throw FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, address);
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

    @Transactional(readOnly = true)
    public GroupedAdminsResponse getAllAdminsGrouped() {
        List<AdminUser> allUsers = adminUserRepository.findAll();

        List<AdminResponse> admins = new ArrayList<>();
        List<AdminResponse> managers = new ArrayList<>();

        for (AdminUser user : allUsers) {
            AdminResponse dto = mapToResponse(user);

            if (user.getRoles() != null && user.getRoles().contains(Role.SUPER_ADMIN)) {
                admins.add(dto);
            } else {
                managers.add(dto);
            }
        }

        return new GroupedAdminsResponse(admins, managers);
    }

    private AdminResponse mapToResponse(AdminUser user) {
        AdminResponse response = new AdminResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        return response;
    }
}
