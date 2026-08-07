package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.constant.AppConstant;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageValidationService {

    private final Tika tika = new Tika();

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw AppException.of(AppErrorCode.INVALID_FILE_FORMAT);
        }
//
//        if (file.getSize() > AppConstant.Upload.MAX_FILE_SIZE) {
//            throw AppException.of(AppErrorCode.FILE_TOO_LARGE);
//        }

        try {
            String detectedType = tika.detect(file.getInputStream());

            if (!AppConstant.Upload.ALLOWED_IMAGE_TYPES.contains(detectedType)) {
                throw AppException.of(AppErrorCode.INVALID_FILE_FORMAT);
            }

        } catch (IOException e) {
            throw AppException.of(AppErrorCode.FILE_PROCESSING_FAILED);
        }
    }
}
