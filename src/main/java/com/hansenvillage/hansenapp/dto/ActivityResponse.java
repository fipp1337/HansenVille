package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.ActivityType;
import lombok.Data;
import java.util.UUID;

@Data
public class ActivityResponse {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private ActivityType type;
}