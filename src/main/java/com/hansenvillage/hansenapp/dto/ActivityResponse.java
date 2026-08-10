package com.hansenvillage.hansenapp.dto;

import com.hansenvillage.hansenapp.entity.ActivityType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ActivityResponse {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private ActivityType type;
    private String phoneNumber;
    private List<DayOfWeek> dayOfWeek;
    private LocalDateTime dateTime;
    private String location;
}