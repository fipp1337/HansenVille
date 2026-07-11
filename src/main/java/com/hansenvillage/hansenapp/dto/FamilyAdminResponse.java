package com.hansenvillage.hansenapp.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FamilyAdminResponse {
    private UUID id;
    private String email;
    private String address;
    private String phoneNumber;
    private Integer memberCount;
}
