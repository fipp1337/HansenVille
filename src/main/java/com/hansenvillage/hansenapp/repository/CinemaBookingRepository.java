package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.CinemaBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CinemaBookingRepository extends JpaRepository<CinemaBooking, UUID> {

    boolean existsByCinemaSessionIdAndSeatId(UUID cinemaSessionId, UUID seatId);

    List<CinemaBooking> findByUserId(UUID userId);

    @Query("SELECT pb FROM CinemaBooking pb JOIN User u ON pb.userId = u.id WHERE u.familyId = :familyId")
    List<CinemaBooking> findByFamilyId(@Param("familyId") UUID familyId);

    @Modifying
    @Query("DELETE FROM CinemaBooking b WHERE b.userId IN :userIds")
    void deleteByUserIdIn(@Param("userIds") List<UUID> userIds);
}
