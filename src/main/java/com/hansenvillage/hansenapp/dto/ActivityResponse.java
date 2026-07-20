package com.hansenvillage.hansenapp.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ActivityResponse {
    private UUID id;
    private String title;
    private String imageUrl;
}