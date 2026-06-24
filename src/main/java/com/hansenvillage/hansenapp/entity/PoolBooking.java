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


    @JoinColumn(nullable = false)
    private long userId;


    @JoinColumn(nullable = false)
    private long poolSessionId;
}