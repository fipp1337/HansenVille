package com.hansenvillage.hansenapp.dto;

import lombok.Data;

@Data
public class InitiateRegistrationRequest {
    private String email;
    private String inviteCode;
}