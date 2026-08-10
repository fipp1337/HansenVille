package com.hansenvillage.hansenapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hansenvillage.hansenapp.entity.ActivityType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityRequest {
    private String title;
    private String description;
    private ActivityType type;
    private String phoneNumber;
    private List<DayOfWeek> dayOfWeek;
    private String location;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateTime;
}