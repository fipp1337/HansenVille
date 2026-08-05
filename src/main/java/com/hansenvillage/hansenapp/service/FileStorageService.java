package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    public String store(MultipartFile file, String directory) {
        try {
            Path uploadDir = Paths.get(directory);
            Files.createDirectories(uploadDir);

            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored: dir={}, file={}", directory, fileName);
            return fileName;
        } catch (IOException e) {
            throw AppException.of(AppErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public Resource loadAsResource(String directory, String fileName, AppErrorCode notFoundCode) {
        if (fileName == null) {
            throw AppException.of(notFoundCode);
        }

        Path path = Paths.get(directory).resolve(fileName);
        try {
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw AppException.of(notFoundCode);
            }
            return resource;
        } catch (IOException e) {
            throw AppException.of(notFoundCode);
        }
    }

    public void delete(String directory, String fileName) {
        try {
            Files.deleteIfExists(Paths.get(directory).resolve(fileName));
            log.info("File deleted: dir={}, file={}", directory, fileName);
        } catch (IOException e) {
            throw AppException.of(AppErrorCode.FILE_DELETE_FAILED);
        }
    }

    public void deleteQuietly(String directory, String fileName) {
        if (fileName == null) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(directory).resolve(fileName));
        } catch (IOException ignored) {
            // best-effort cleanup
        }
    }
}
