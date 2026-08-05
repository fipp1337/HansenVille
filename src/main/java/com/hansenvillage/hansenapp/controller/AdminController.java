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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    @GetMapping("/families/search")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public List<FamilyInfoResponse> findFamiliesByAddress(@RequestParam String address) {
        return adminService.findFamiliesByAddress(address);
    }

    @DeleteMapping("/families")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void deleteFamilyByAddress(@RequestParam String address) {
        adminService.deleteFamilyByAddress(address);
    }

    @PostMapping
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String createAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        adminService.addAdmin(request);
        return "Admin registered successfully";
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AdminUpdateResponse updateAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateRequest request) {
        return adminService.updateAdmin(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void deleteAdmin(@PathVariable UUID id) {
        adminService.deleteAdmin(id);
    }

    @GetMapping
//     @PreAuthorize("hasRole('SUPER_ADMIN')")
    public GroupedAdminsResponse getAllAdmins() {
        return adminService.getAllAdminsGrouped();
    }

    @PostMapping("/family-registration")
    @ResponseStatus(HttpStatus.CREATED)
    //  @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String registerFamilyByAdmin(@Valid @RequestBody FamilyAdminRegistrationRequest request) {
        authService.registrationFamilyByAdmin(request);
        return "Family registered successfully";
    }
}
