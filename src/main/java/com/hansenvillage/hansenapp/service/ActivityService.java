package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.ActivityRequest;
import com.hansenvillage.hansenapp.dto.ActivityResponse;
import com.hansenvillage.hansenapp.entity.Activity;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private static final Path UPLOAD_DIR = Paths.get("uploads/activities");

    @Transactional(readOnly = true)
    public List<ActivityResponse> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ActivityResponse createActivity(ActivityRequest request) {
        Activity activity = new Activity();
        activity.setTitle(request.getTitle());

        Activity saved = activityRepository.save(activity);
        return mapToResponse(saved);
    }

    @Transactional
    public ActivityResponse updateActivity(UUID id, ActivityRequest request) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.ACTIVITY_NOT_FOUND, id));

        activity.setTitle(request.getTitle());

        return mapToResponse(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.ACTIVITY_NOT_FOUND, id));

        deleteImageFileSilently(activity.getImage());
        activityRepository.delete(activity);
    }

    @Transactional
    public void uploadImage(UUID id, MultipartFile file) {
        try {
            Activity activity = activityRepository.findById(id)
                    .orElseThrow(() -> FamilyException.of(FamilyErrorCode.ACTIVITY_NOT_FOUND, id));

            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Files.createDirectories(UPLOAD_DIR);

            Files.copy(
                    file.getInputStream(),
                    UPLOAD_DIR.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            deleteImageFileSilently(activity.getImage());

            activity.setImage(fileName);
            activityRepository.save(activity);
        } catch (IOException e) {
            throw FamilyException.of(FamilyErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public ResponseEntity<Resource> getImage(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.ACTIVITY_NOT_FOUND, id));

        if (activity.getImage() == null) {
            throw FamilyException.of(FamilyErrorCode.ACTIVITY_IMAGE_NOT_FOUND);
        }

        Path path = UPLOAD_DIR.resolve(activity.getImage());

        try {
            Resource resource = new UrlResource(path.toUri());
            String contentType = Files.probeContentType(path);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            throw FamilyException.of(FamilyErrorCode.ACTIVITY_IMAGE_NOT_FOUND);
        }
    }

    private void deleteImageFileSilently(String fileName) {
        if (fileName != null) {
            try {
                Files.deleteIfExists(UPLOAD_DIR.resolve(fileName));
            } catch (IOException ignored) {
            }
        }
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setTitle(activity.getTitle());

        if (activity.getImage() != null) {
            response.setImageUrl("/api/activities/image/" + activity.getId());
        }
        return response;
    }

    @Transactional
    public void updateImage(UUID id, MultipartFile newFile) {
        uploadImage(id, newFile);
    }

    @Transactional
    public void deleteImage(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.ACTIVITY_NOT_FOUND, id));

        if (activity.getImage() == null) {
            throw FamilyException.of(FamilyErrorCode.ACTIVITY_IMAGE_NOT_FOUND);
        }

        deleteImageFileSilently(activity.getImage());
        activity.setImage(null);
        activityRepository.save(activity);
    }
}