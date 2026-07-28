package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.ActivityRequest;
import com.hansenvillage.hansenapp.dto.ActivityResponse;
import com.hansenvillage.hansenapp.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ActivityResponse>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getActivityImage(@PathVariable UUID id) {
        return activityService.getImage(id);
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.createActivity(request));
    }

    @PostMapping("/image/{id}")
    public ResponseEntity<Void> uploadActivityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        activityService.uploadImage(id, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponse> updateActivity(
            @PathVariable UUID id,
            @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.updateActivity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<Void> updateActivityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        activityService.updateImage(id, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/image/{id}")
    public ResponseEntity<Void> deleteActivityImage(@PathVariable UUID id) {
        activityService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}