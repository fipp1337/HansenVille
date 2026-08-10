package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cinema_bookings")
public class CinemaBooking {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private UUID cinemaSessionId;

    private UUID seatId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private CinemaBookingStatus status;
}