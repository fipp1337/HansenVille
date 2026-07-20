package com.hansenvillage.hansenapp.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class FacilityResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String imageUrl;
    private String targetRoute;
    private boolean active;
}