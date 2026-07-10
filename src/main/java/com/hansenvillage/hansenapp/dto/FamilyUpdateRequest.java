package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import lombok.Data;

import java.util.List;

@Data
public class FamilyUpdateRequest {
    private String password;
    private String email;
    private String address;
    private String phoneNumber;
}
