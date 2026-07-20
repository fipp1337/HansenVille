package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class FamilyRegistrationRequest {

//    @NotBlank
//    @Size(min = 6, max = 25)
//    private String password;
//
//    @NotBlank
//    @Size(min = 6, max = 25)
//    private String confirmPassword;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String address;

    private String phoneNumber;

    private List<Role> roles;

    private String verificationCode;
}