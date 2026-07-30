package com.hansenvillage.hansenapp.dto;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FamilyUpdateRequest {
    private String oldPassword;
    @Size(min = 6, max = 25, message = "password minimum length 5 symbols, maximum - 25")
    private String newPassword;
    private String confirmNewPassword;
    private String email;
    private String address;
    private String phoneNumber;
}
