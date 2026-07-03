package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "cinema_sessions")
public class CinemaSession {

    @Id
    @GeneratedValue
    private UUID id;

    private String movieName;

    private LocalTime startTime;

    private int duration;

    private int maxCapacity;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private LocalDate sessionDate;

    private int bookedCount = 0;

    @Version
    private Integer version;
}