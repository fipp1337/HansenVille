package com.hansenvillage.hansenapp.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserUpdateResponse {
    private UUID id;
    private String name;
    private Integer age;
}
