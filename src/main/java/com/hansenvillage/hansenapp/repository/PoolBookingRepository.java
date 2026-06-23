package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolBookingRepository extends JpaRepository<PoolBooking, Long> {

}
