package com.hansenvillage.hansenapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class FamilyAdminRegistrationRequest {
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 6, max = 25, message = "Minimal password size - 6 symbols, maximum - 25")
    private String password;
    @NotBlank
    private String address;

    private String phoneNumber;
}
