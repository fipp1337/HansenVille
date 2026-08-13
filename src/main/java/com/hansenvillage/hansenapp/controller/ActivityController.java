package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.ActivityRequest;
import com.hansenvillage.hansenapp.dto.ActivityResponse;
import com.hansenvillage.hansenapp.entity.ActivityType;
import com.hansenvillage.hansenapp.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public List<ActivityResponse> getActivities(@RequestParam(required = false) ActivityType type) {
        return activityService.getAllActivities(type);
    }


    @GetMapping("/me")
    public List<ActivityResponse> getMyActivities() {
        return activityService.getMyActivities();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ACTIVITY')")
    public ActivityResponse createActivity(@RequestBody @Valid ActivityRequest request) {
        return activityService.createActivity(request);
    }

    @PostMapping(value = "/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadActivityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        activityService.uploadImage(id, file);
    }

    @PutMapping("/{id}")
    public ActivityResponse updateActivity(
            @PathVariable UUID id,
            @RequestBody @Valid ActivityRequest request) {
        return activityService.updateActivity(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> getActivityImage(@PathVariable UUID id) {
        Resource resource = activityService.getImage(id);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @PutMapping(value = "/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateActivityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        activityService.updateImage(id, file);
    }

    @DeleteMapping("/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivityImage(@PathVariable UUID id) {
        activityService.deleteImage(id);
    }
}