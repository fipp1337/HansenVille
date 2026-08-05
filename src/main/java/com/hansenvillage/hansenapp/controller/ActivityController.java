package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.ActivityRequest;
import com.hansenvillage.hansenapp.dto.ActivityResponse;
import com.hansenvillage.hansenapp.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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
    public List<ActivityResponse> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/image/{id}")
    public Resource getActivityImage(@PathVariable UUID id) {
        return activityService.getImage(id);
    }

    @PostMapping
    public ActivityResponse createActivity(@RequestBody ActivityRequest request) {
        return activityService.createActivity(request);
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadActivityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        activityService.uploadImage(id, file);
    }

    @PutMapping("/{id}")
    public ActivityResponse updateActivity(
            @PathVariable UUID id,
            @RequestBody ActivityRequest request) {
        return activityService.updateActivity(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
    }

    @PutMapping("/image/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateActivityImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        activityService.updateImage(id, file);
    }

    @DeleteMapping("/image/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivityImage(@PathVariable UUID id) {
        activityService.deleteImage(id);
    }
}