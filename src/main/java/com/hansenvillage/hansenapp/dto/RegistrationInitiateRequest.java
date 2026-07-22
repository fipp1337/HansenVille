package com.hansenvillage.hansenapp.dto;

import lombok.Data;

@Data
public class RegistrationInitiateRequest {
    private String email;
    private String inviteCode;
}