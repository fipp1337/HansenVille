package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.service.AdminService;
import com.hansenvillage.hansenapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/families/search")
    public List<FamilyInfoResponse> findFamiliesByAddress(@RequestParam String address) {
        return adminService.findFamiliesByAddress(address);
    }

    @DeleteMapping("/families")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFamilyByAddress(@RequestParam String address) {
        adminService.deleteFamilyByAddress(address);
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

    @GetMapping
    public GroupedAdminsResponse getAllAdmins() {
        return adminService.getAllAdminsGrouped();
    }

    @PostMapping("/family-registration")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse registerFamilyByAdmin(
            @Valid @RequestBody FamilyAdminRegistrationRequest request) {
        authService.registrationFamilyByAdmin(request);
        return new MessageResponse("Family registered successfully");
    }

//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/families/{familyId}/roles/activity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setActivityRole(@PathVariable UUID familyId) {
        adminService.setActivityRole(familyId);
    }

//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/families/{familyId}/roles/activity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeActivityRole(@PathVariable UUID familyId) {
        adminService.removeActivityRole(familyId);
    }
}
