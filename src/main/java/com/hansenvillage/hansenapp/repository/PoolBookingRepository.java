package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.UUID;

@Repository
public interface PoolBookingRepository extends JpaRepository<PoolBooking, UUID> {

    boolean existsByUserIdAndPoolSessionId(UUID userId, UUID poolSessionId); // Важная штука против повторной записи

    List<PoolBooking> findByUserId(UUID id);
}
