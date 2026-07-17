package com.hansenvillage.hansenapp.dto;

import lombok.Data;

@Data
public class FamilyUpdateRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
    private String email;
    private String address;
    private String phoneNumber;
}
