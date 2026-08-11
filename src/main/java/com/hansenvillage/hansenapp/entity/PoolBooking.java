package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "pool_bookings")
public class PoolBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private UUID poolSessionId;

    @Enumerated(EnumType.STRING)
    private PoolBookingStatus status = PoolBookingStatus.REGISTERED;
}