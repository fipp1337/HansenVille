package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.service.AdminService;
import com.hansenvillage.hansenapp.service.AuthService;
import com.hansenvillage.hansenapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public GroupedAdminsResponse getAllAdmins() {
        return adminService.getAllAdminsGrouped();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createAdmin(
            @Valid @RequestBody AdminRegistrationRequest request) {
        adminService.addAdmin(request);
        return new MessageResponse("Admin registered successfully");
    }

    @PutMapping("/{id}")
    public AdminUpdateResponse updateAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateRequest request) {
        return adminService.updateAdmin(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdmin(@PathVariable UUID id) {
        adminService.deleteAdmin(id);
    }

    @GetMapping("/families")
    public List<FamilyInfoResponse> findFamiliesByAddress(@RequestParam String address) {
        return adminService.findFamiliesByAddress(address);
    }

    @PostMapping("/families")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse registerFamilyByAdmin(
            @Valid @RequestBody FamilyAdminRegistrationRequest request) {
        authService.registrationFamilyByAdmin(request);
        return new MessageResponse("Family registered successfully");
    }

    @PostMapping("/families/{familyId}/roles/activity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setActivityRole(@PathVariable UUID familyId) {
        adminService.setActivityRole(familyId);
    }

    @DeleteMapping("/families/{familyId}/roles/activity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeActivityRole(@PathVariable UUID familyId) {
        adminService.removeActivityRole(familyId);
    }

    @DeleteMapping("/families")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFamilyByAddress(@RequestParam String address) {
        adminService.deleteFamilyByAddress(address);
    }

    @PostMapping("/families/{id}/members")
    public UserUpdateResponse userAddById(@PathVariable UUID id, @Valid @RequestBody AddMemberRequest request) {
        User created = userService.addNewMemberById(id, request);
        return userMapper.toUpdateResponse(created);
    }
}
