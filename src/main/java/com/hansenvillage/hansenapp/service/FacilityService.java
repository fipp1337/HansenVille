package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.FacilityResponse;
import com.hansenvillage.hansenapp.entity.Facility;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.FacilityRepository;
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
public class FacilityService {

    private final FacilityRepository facilityRepository;

    @Transactional
    public List<FacilityResponse> getAllActiveFacilities() {
        return facilityRepository.findAll().stream()
                .filter(Facility::isActive)
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void uploadImage(UUID facilityId, MultipartFile file) {

        try {
            Facility facility = facilityRepository.findById(facilityId)
                    .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FACILITY_NOT_FOUND, facilityId));

            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            Path uploadDir = Paths.get("uploads/facilities");

            Files.createDirectories(uploadDir);

            Files.copy(
                    file.getInputStream(),
                    uploadDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            facility.setImage(fileName);

            facilityRepository.save(facility);
        } catch (IOException e) {
            throw FamilyException.of(FamilyErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public ResponseEntity<Resource> getImage(UUID facilityId) {

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FACILITY_NOT_FOUND, facilityId));

        if (facility.getImage() == null) {
            throw FamilyException.of(FamilyErrorCode.FACILITY_IMAGE_NOT_FOUND);
        }

        Path path = Paths.get("uploads/facilities")
                .resolve(facility.getImage());

        try {

            Resource resource = new UrlResource(path.toUri());

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {

            throw FamilyException.of(FamilyErrorCode.FACILITY_IMAGE_NOT_FOUND);
        }
    }

    public void updateImage(UUID facilityId, MultipartFile newFile) {
        deleteImage(facilityId);
        uploadImage(facilityId, newFile);
    }

    public void deleteImage(UUID facilityId) {

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FACILITY_NOT_FOUND, facilityId));

        if (facility.getImage() == null) {
            throw FamilyException.of(FamilyErrorCode.FACILITY_IMAGE_NOT_FOUND);
        }

        try {
            Path path = Paths.get("uploads/facilities")
                    .resolve(facility.getImage());

            Files.deleteIfExists(path);

            facility.setImage(null);
            facilityRepository.save(facility);

        } catch (IOException e) {
            throw FamilyException.of(FamilyErrorCode.FILE_DELETE_FAILED);
        }
    }

    private FacilityResponse mapToResponse(Facility facility) {
        FacilityResponse response = new FacilityResponse();
        response.setId(facility.getId());
        response.setCode(facility.getCode());
        response.setName(facility.getName());
        response.setDescription(facility.getDescription());
        response.setTargetRoute(facility.getTargetRoute());
        response.setActive(facility.isActive());

        if (facility.getImage() != null) {
            response.setImageUrl("/api/facilities/image/" + facility.getId());
        }
        return response;
    }
}