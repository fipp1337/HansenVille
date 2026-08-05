package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.constant.AppConstant;
import com.hansenvillage.hansenapp.dto.FacilityResponse;
import com.hansenvillage.hansenapp.entity.Facility;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.FacilityMapper;
import com.hansenvillage.hansenapp.repository.FacilityRepository;
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
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<FacilityResponse> getAllActiveFacilities() {
        return facilityRepository.findByActiveTrue().stream()
                .map(facilityMapper::toResponse)
                .toList();
    }

    @Transactional
    public void uploadImage(UUID facilityId, MultipartFile file) {
        Facility facility = findFacility(facilityId);
        facility.setImage(fileStorageService.store(file, AppConstant.Upload.FACILITIES_DIR));
        facilityRepository.save(facility);
        log.info("Facility image uploaded: {}", facilityId);
    }

    @Transactional(readOnly = true)
    public Resource getImage(UUID facilityId) {
        Facility facility = findFacility(facilityId);
        return fileStorageService.loadAsResource(
                AppConstant.Upload.FACILITIES_DIR,
                facility.getImage(),
                AppErrorCode.FACILITY_IMAGE_NOT_FOUND
        );
    }

    @Transactional
    public void updateImage(UUID facilityId, MultipartFile newFile) {
        deleteImage(facilityId);
        uploadImage(facilityId, newFile);
    }

    @Transactional
    public void deleteImage(UUID facilityId) {
        Facility facility = findFacility(facilityId);
        if (facility.getImage() == null) {
            throw AppException.of(AppErrorCode.FACILITY_IMAGE_NOT_FOUND);
        }

        fileStorageService.delete(AppConstant.Upload.FACILITIES_DIR, facility.getImage());
        facility.setImage(null);
        facilityRepository.save(facility);
        log.info("Facility image deleted: {}", facilityId);
    }

    private Facility findFacility(UUID facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> AppException.of(AppErrorCode.FACILITY_NOT_FOUND, facilityId));
    }
}
