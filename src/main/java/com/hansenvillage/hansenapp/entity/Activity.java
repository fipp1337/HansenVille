package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Data
public class Activity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 150)
    private String description;

    private String image;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<DayOfWeek> dayOfWeek;

    private String location;
}