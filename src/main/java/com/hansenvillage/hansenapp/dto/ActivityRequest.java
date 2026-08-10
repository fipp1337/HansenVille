package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.ActivityType;
import lombok.Data;

@Data
public class ActivityRequest {
    private String title;
    private String description;
    private ActivityType type;
}