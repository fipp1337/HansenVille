package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FamilyPasswordService {

    private final PasswordEncoder passwordEncoder;

    public void updatePassword(Family family, String oldPassword, String newPassword, String confirmPassword) {
        validate(oldPassword, newPassword, confirmPassword, family);

        family.setPassword(passwordEncoder.encode(newPassword));
    }

    private void validate(String oldPassword, String newPassword, String confirmPassword, Family family) {
        if (!hasText(oldPassword)) {
            throw AppException.of(AppErrorCode.OLD_PASSWORD_REQUIRED);
        }

        if (!hasText(newPassword)) {
            throw AppException.of(AppErrorCode.NEW_PASSWORD_REQUIRED);
        }

        if (!hasText(confirmPassword)) {
            throw AppException.of(AppErrorCode.CONFIRM_PASSWORD_REQUIRED);
        }

        if (!passwordEncoder.matches(oldPassword, family.getPassword())) {
            throw AppException.of(AppErrorCode.INVALID_OLD_PASSWORD);
        }

        if (newPassword.equals(oldPassword)) {
            throw AppException.of(AppErrorCode.NEW_PASSWORD_MATCH_WITH_OLD);
        }

        if (!newPassword.equals(confirmPassword)) {
            throw AppException.of(AppErrorCode.PASSWORDS_DO_NOT_MATCH);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}