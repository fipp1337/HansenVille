package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "pool_bookings")
public class PoolBooking {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private UUID poolSessionId;

    @Enumerated(EnumType.STRING)
    private PoolBookingStatus status = PoolBookingStatus.REGISTERED;
}