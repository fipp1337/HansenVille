package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
}