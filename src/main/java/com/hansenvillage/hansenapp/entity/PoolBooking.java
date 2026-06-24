package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pool_booking")
public class PoolBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    private long poolSessionId;
}