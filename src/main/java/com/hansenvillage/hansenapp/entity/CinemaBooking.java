package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

//    status     VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',

    private LocalDateTime createdAt = LocalDateTime.now();
}