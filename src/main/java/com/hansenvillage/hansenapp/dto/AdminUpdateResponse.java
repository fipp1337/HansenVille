package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdminUpdateResponse {
    private String name;
    private String email;
    private Set<Role> roles;
}
