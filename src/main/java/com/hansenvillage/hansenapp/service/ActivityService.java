package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.constant.AppConstant;
import com.hansenvillage.hansenapp.dto.ActivityRequest;
import com.hansenvillage.hansenapp.dto.ActivityResponse;
import com.hansenvillage.hansenapp.entity.Activity;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.ActivityMapper;
import com.hansenvillage.hansenapp.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<ActivityResponse> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(activityMapper::toResponse)
                .toList();
    }

    @Transactional
    public ActivityResponse createActivity(ActivityRequest request) {
        Activity saved = activityRepository.save(activityMapper.toEntity(request));
        log.info("Activity created: id={}, title={}", saved.getId(), saved.getTitle());
        return activityMapper.toResponse(saved);
    }

    @Transactional
    public ActivityResponse updateActivity(UUID id, ActivityRequest request) {
        Activity activity = findActivity(id);
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        return activityMapper.toResponse(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(UUID id) {
        Activity activity = findActivity(id);
        fileStorageService.deleteQuietly(AppConstant.Upload.ACTIVITIES_DIR, activity.getImage());
        activityRepository.delete(activity);
        log.info("Activity deleted: {}", id);
    }

    @Transactional
    public void uploadImage(UUID id, MultipartFile file) {
        Activity activity = findActivity(id);
        String fileName = fileStorageService.store(file, AppConstant.Upload.ACTIVITIES_DIR);
        fileStorageService.deleteQuietly(AppConstant.Upload.ACTIVITIES_DIR, activity.getImage());
        activity.setImage(fileName);
        activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> getImage(UUID id) {
        Activity activity = findActivity(id);
        return fileStorageService.loadAsResource(
                AppConstant.Upload.ACTIVITIES_DIR,
                activity.getImage(),
                AppErrorCode.ACTIVITY_IMAGE_NOT_FOUND
        );
    }

    @Transactional
    public void updateImage(UUID id, MultipartFile newFile) {
        uploadImage(id, newFile);
    }

    @Transactional
    public void deleteImage(UUID id) {
        Activity activity = findActivity(id);
        if (activity.getImage() == null) {
            throw AppException.of(AppErrorCode.ACTIVITY_IMAGE_NOT_FOUND);
        }

        fileStorageService.deleteQuietly(AppConstant.Upload.ACTIVITIES_DIR, activity.getImage());
        activity.setImage(null);
        activityRepository.save(activity);
    }

    private Activity findActivity(UUID id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.ACTIVITY_NOT_FOUND, id));
    }
}
