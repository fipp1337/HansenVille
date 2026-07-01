package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

}