package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

    private String description;

    private Integer year;
    private String ageRating;
    private int duration;

    @Enumerated(EnumType.STRING)
    private List<CinemaSessionGenre> genre;

    private UUID hallId;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    private CinemaSessionType type;

    @Version
    private Integer version;
}