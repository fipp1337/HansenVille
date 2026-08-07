package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.FacilityResponse;
import com.hansenvillage.hansenapp.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
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
    public List<FacilityResponse> getAllFacilities() {
        return facilityService.getAllActiveFacilities();
    }

    @PostMapping(value = "/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        facilityService.uploadImage(id, file);
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> getFacilityImage(@PathVariable UUID id) {
        Resource resource = facilityService.getImage(id);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @PutMapping(value = "/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void updateFacilityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        facilityService.updateImage(id, file);
    }

    @DeleteMapping("/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void deleteFacilityImage(@PathVariable UUID id) {
        facilityService.deleteImage(id);
    }
}