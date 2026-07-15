package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.FamilyAdminResponse;
import com.hansenvillage.hansenapp.dto.FamilyInfoResponse;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.service.AdminService;
import com.hansenvillage.hansenapp.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final FamilyMapper familyMapper;

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
}