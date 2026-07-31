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

    List<CinemaBooking> findByUserIdIn(List<UUID> userIds);

    List<CinemaBooking> findByCinemaSessionId(UUID id);

    @Query("""
            SELECT b.seatId
            FROM CinemaBooking b
            WHERE b.cinemaSessionId = :sessionId
            """)
    List<UUID> findSeatIdsByCinemaSessionId(@Param("sessionId") UUID sessionId);

    boolean existsByCinemaSessionIdAndSeatId(UUID cinemaSessionId, UUID seatId);

    @Query("""
            FROM CinemaBooking b
            JOIN CinemaSession session ON b.cinemaSessionId = session.id
            WHERE b.userId = :userId
              AND session.startAt > CURRENT_TIMESTAMP
            ORDER BY session.startAt
            """)
    List<CinemaBooking> findFutureByUserId(@Param("userId") UUID userId);

    List<CinemaBooking> getBookingsByCinemaSessionId(UUID cinemaSessionId);

    @Modifying
    @Query("delete from CinemaBooking b where b.cinemaSessionId = :sessionId")
    void deleteByCinemaSessionId(UUID sessionId);

    @Modifying
    @Query("DELETE FROM CinemaBooking b WHERE b.userId IN :userIds")
    void deleteByUserIdIn(@Param("userIds") List<UUID> userIds);
}