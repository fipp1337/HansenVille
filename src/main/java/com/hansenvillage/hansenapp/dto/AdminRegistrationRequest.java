package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdminRegistrationRequest {
    @NotBlank
    private String email;
    @NotEmpty
    private Set<Role> roles;
    @NotBlank
    private String name;
}
