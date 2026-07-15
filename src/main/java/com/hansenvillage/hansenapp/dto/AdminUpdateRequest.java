package com.hansenvillage.hansenapp.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdminUpdateRequest {
    private String name;
    private String email;
    private String password;
    private List<String> roles;
}