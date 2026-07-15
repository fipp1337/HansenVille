package com.hansenvillage.hansenapp.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class FamilyInfoResponse {
    private UUID id;
    private String email;
    private String address;
    private String phoneNumber;
    private int memberCount;
    private List<UserResponse> members;
}