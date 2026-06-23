package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PoolBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pool_sessions_id", nullable = false, unique = true)
    private PoolSession poolSession;
}