package com.hansenvillage.hansenapp.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String name;
    private Integer age;
//    private LocalDate createdAt;
}