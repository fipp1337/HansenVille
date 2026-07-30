package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.service.AdminService;
import com.hansenvillage.hansenapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<FamilyInfoResponse>> findFamiliesByAddress(@RequestParam String address) {
        return ResponseEntity.ok(adminService.findFamiliesByAddress(address));
    }

    @DeleteMapping("/families")
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteFamilyByAddress(@RequestParam String address) {
        adminService.deleteFamilyByAddress(address);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        adminService.addAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Admin registered successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUpdateResponse> updateAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
//     @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<GroupedAdminsResponse> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdminsGrouped());
    }

    @PostMapping("/family-registration")
    //  @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> registerFamilyByAdmin(@Valid @RequestBody FamilyAdminRegistrationRequest request) {
        authService.registrationFamilyByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Family registered successfully");
    }
}
