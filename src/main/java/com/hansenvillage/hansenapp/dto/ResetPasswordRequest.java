package com.hansenvillage.hansenapp.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String verificationCode;
    private String newPassword;
//    private String repeatPassword;
}
