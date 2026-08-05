package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.FacilityResponse;
import com.hansenvillage.hansenapp.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public List<FacilityResponse> getAllFacilities() {
        return facilityService.getAllActiveFacilities();
    }

    @GetMapping(value = "/image/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Resource getFacilityImage(@PathVariable UUID id) {
        return facilityService.getImage(id);
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public void uploadFacilityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        facilityService.uploadImage(id, file);
    }

    @PutMapping("/image/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public void updateFacilityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        facilityService.updateImage(id, file);
    }

    @DeleteMapping("/image/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public void deleteFacilityImage(@PathVariable UUID id) {
        facilityService.deleteImage(id);
    }
}