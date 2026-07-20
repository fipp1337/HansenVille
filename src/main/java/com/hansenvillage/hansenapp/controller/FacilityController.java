package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.FacilityResponse;
import com.hansenvillage.hansenapp.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public ResponseEntity<List<FacilityResponse>> getAllFacilities() {
        return ResponseEntity.ok(facilityService.getAllActiveFacilities());
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getFacilityImage(@PathVariable UUID id) {
        return facilityService.getImage(id);
    }

    @PostMapping("/image/{id}")
    public ResponseEntity<Void> uploadFacilityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        facilityService.uploadImage(id, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<Void> updateFacilityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        facilityService.updateImage(id, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/image/{id}")
    public ResponseEntity<Void> deleteFacilityImage(@PathVariable UUID id) {
        facilityService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}