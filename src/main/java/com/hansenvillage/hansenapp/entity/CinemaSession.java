package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cinema_sessions")
public class CinemaSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String movieName;

    private String posterImage;

    private LocalDateTime startAt;

    private int duration;

    private String description;

    private UUID hallId;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Version
    private Integer version;
}