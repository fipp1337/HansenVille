package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class AdminResponse {
    private UUID id;
    private String name;
    private String email;
    private Set<Role> roles;
}