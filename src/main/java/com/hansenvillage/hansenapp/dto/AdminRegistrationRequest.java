package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
@Data
public class AdminRegistrationRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private Set<Role> roles;
}
