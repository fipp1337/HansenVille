package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // posterImage

    private LocalDateTime startAt;

    private int duration;

    private UUID hallId;
    
    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Version
    private Integer version;
}