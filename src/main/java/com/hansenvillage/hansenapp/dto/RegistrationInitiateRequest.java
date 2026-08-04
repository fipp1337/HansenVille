package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationInitiateRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String address;
    @NotBlank
    private String inviteCode;
}