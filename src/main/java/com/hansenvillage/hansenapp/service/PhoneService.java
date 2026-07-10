package com.hansenvillage.hansenapp.service;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneService {
    private final PhoneNumberUtil phoneUtil;

    public String validateAndFormatPhone(String phone) {
        try {
            var number = phoneUtil.parse(phone, "UA");

            if (!phoneUtil.isValidNumber(number)) {
                throw FamilyException.of(FamilyErrorCode.INVALID_PHONE_FORMAT);
            }
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);

        } catch (Exception e) {
            throw FamilyException.of(FamilyErrorCode.INVALID_PHONE_FORMAT);
        }
    }
}