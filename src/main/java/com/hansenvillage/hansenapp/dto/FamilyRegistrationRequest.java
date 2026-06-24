package com.hansenvillage.hansenapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class FamilyRegistrationRequest {

    @NotBlank(message = "Login is blank")
    private String username;

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

    @Data
    public static class MemberRequest {
        @NotBlank(message = "Fullname is blank")
        private String fullName;
    }
}