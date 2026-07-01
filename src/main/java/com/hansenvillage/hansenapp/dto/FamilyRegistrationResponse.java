package com.hansenvillage.hansenapp.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FamilyRegistrationResponse {
    private UUID id;
    private String email;
    private String address;
}
