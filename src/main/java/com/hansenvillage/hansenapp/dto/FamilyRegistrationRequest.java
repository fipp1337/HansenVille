package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class FamilyRegistrationRequest {

    @NotBlank(message = "Password is blank")
    private String password;

    @NotBlank
    @Email(message = "Invalid email")
    private String email;

    @NotBlank
    private String address;

    @NotEmpty(message = "You need add family member")
    @Valid
    private List<MemberRequest> members;

    private List<Role> roles;

    @Data
    public static class MemberRequest {
        @NotBlank(message = "Name is blank")
        private String name;
    }
}