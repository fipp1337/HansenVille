package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AdminRegistrationRequest;
import com.hansenvillage.hansenapp.dto.AdminResponse;
import com.hansenvillage.hansenapp.dto.AdminUpdateRequest;
import com.hansenvillage.hansenapp.dto.AdminUpdateResponse;
import com.hansenvillage.hansenapp.dto.FamilyInfoResponse;
import com.hansenvillage.hansenapp.dto.GroupedAdminsResponse;
import com.hansenvillage.hansenapp.entity.AdminUser;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.Role;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.AdminMapper;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.AdminUserRepository;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminUserRepository adminUserRepository;
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final AdminMapper adminMapper;
    private final FamilyMapper familyMapper;
    private final UserMapper userMapper;
    private final FamilyService familyService;

    @Transactional
    public void addAdmin(AdminRegistrationRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (email.isBlank()) {
            throw AppException.of(AppErrorCode.WRONG_EMAIL);
        }
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw AppException.of(AppErrorCode.ROLES_EMPTY);
        }
        if (adminUserRepository.existsByEmail(email)) {
            throw AppException.of(AppErrorCode.EMAIL_ALREADY_EXISTS, email);
        }

        AdminUser admin = adminMapper.toEntity(request);
        admin.setEmail(email);
        adminUserRepository.save(admin);
        log.info("Admin created: email={}, roles={}", email, request.getRoles());
    }

    @Transactional
    public AdminUpdateResponse updateAdmin(UUID adminId, AdminUpdateRequest request) {
        AdminUser admin = adminUserRepository.findById(adminId)
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, adminId));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(admin.getEmail())) {
                if (adminUserRepository.existsByEmail(newEmail)) {
                    throw AppException.of(AppErrorCode.EMAIL_ALREADY_EXISTS, newEmail);
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
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND));
        adminUserRepository.delete(admin);
        log.info("Admin deleted: {}", adminId);
    }

    @Transactional(readOnly = true)
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
            throw AppException.of(AppErrorCode.FAMILY_NOT_FOUND, address);
        }
        families.forEach(family -> familyService.deleteFamilyData(family.getId()));
        log.info("Families deleted by address: address={}, count={}", address, families.size());
    }

    @Transactional(readOnly = true)
    public GroupedAdminsResponse getAllAdminsGrouped() {
        List<AdminResponse> admins = new ArrayList<>();
        List<AdminResponse> managers = new ArrayList<>();

        for (AdminUser user : adminUserRepository.findAll()) {
            AdminResponse response = toAdminResponse(user);
            if (user.getRoles() != null && user.getRoles().contains(Role.SUPER_ADMIN)) {
                admins.add(response);
            } else {
                managers.add(response);
            }
        }

        return new GroupedAdminsResponse(admins, managers);
    }

    private AdminResponse toAdminResponse(AdminUser user) {
        AdminResponse response = new AdminResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        return response;
    }
}
