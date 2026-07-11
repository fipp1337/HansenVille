package com.hansenvillage.hansenapp.dto;

import lombok.Data;

@Data
public class FamilyUpdateRequest {
    private String password;
    private String email;
    private String address;
    private String phoneNumber;
}
