package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.constant.AppConstant;
import com.hansenvillage.hansenapp.dto.ActivityRequest;
import com.hansenvillage.hansenapp.dto.ActivityResponse;
import com.hansenvillage.hansenapp.entity.Activity;
import com.hansenvillage.hansenapp.entity.ActivityType;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.ActivityMapper;
import com.hansenvillage.hansenapp.repository.ActivityRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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
    private final ImageValidationService imageValidationService;

    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivities(ActivityType type) {
        List<Activity> activities;
        if (type != null) {
            activities = activityRepository.findByType(type);
        } else {
            activities = activityRepository.findAll();
        }

        return activities.stream()
                .map(activityMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> getMyActivities() {
        UUID currentFamilyId = SecurityUtils.currentFamilyId();

        return activityRepository.findByFamilyId(currentFamilyId).stream()
                .map(activityMapper::toResponse)
                .toList();
    }

    @Transactional
    public ActivityResponse createActivity(ActivityRequest request) {
        UUID currentFamilyId = SecurityUtils.currentFamilyId();
        Activity activity = activityMapper.toEntity(request);
        activity.setFamilyId(currentFamilyId);
        Activity saved = activityRepository.save(activity);

        log.info("Activity created: id={}, title={}, familyId={}", saved.getId(), saved.getTitle(), currentFamilyId);
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
        imageValidationService.validate(file);
        Activity activity = findActivity(id);
        String fileName = fileStorageService.store(file, AppConstant.Upload.ACTIVITIES_DIR);
        fileStorageService.deleteQuietly(AppConstant.Upload.ACTIVITIES_DIR, activity.getImage());
        activity.setImage(fileName);
        activityRepository.save(activity);
        log.info("Activity image uploaded: id={}", id);
    }

    @Transactional(readOnly = true)
    public Resource getImage(UUID id) {
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