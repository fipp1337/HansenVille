package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AdminRegistrationRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotEmpty
    private List<Role> roles;
    @NotBlank
    private String name;
}
