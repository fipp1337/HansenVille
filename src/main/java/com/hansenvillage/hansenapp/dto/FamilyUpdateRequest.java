package com.hansenvillage.hansenapp.dto;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FamilyUpdateRequest {
    private String oldPassword;
    @Size(min = 6, max = 25, message = "password minimum length 6 symbols, maximum - 25")
    private String newPassword;
    private String confirmNewPassword;
    private String email;
//    @Pattern(
//            regexp = "^(?:A(?:[1-9]/[12]|1[0-5]|20)|B(?:1|[2-8]/[12]|9|1[0-9]|20)|C(?:[1-8]|(?:9|10|11)/[12]|12)|D(?:[1-5]|(?:7|8|9|1[0-5])/[12])|Е[1-3])$",
//            message = "Invalid address format"
//    )
    private String address;
    private String phoneNumber;
}
