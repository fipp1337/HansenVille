package com.hansenvillage.hansenapp.dto;

import lombok.Data;

@Data
public class FamilyRegistrationResponse {
    private Long id;
    private String email;
    private String address;
    private int memberCount;
}
