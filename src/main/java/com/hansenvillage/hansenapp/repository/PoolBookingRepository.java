package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PoolBookingRepository extends JpaRepository<PoolBooking, UUID> {

    List<PoolBooking> findByUserIdIn(List<UUID> userIds);

    Optional<PoolBooking> findByUserIdAndPoolSessionId(UUID userId, UUID poolSessionId);

    List<PoolBooking> findByPoolSessionId(UUID poolSessionId);

    List<PoolBooking> findByUserIdInAndStatus(List<UUID> userIds, PoolBookingStatus status);

    List<PoolBooking> findByUserIdAndStatus(UUID userId, PoolBookingStatus status);

    @Modifying
    @Query("DELETE FROM PoolBooking b WHERE b.poolSessionId = :poolSessionId")
    void deleteByPoolSessionId(@Param("poolSessionId") UUID poolSessionId);

    @Modifying
    @Query("DELETE FROM PoolBooking b WHERE b.userId IN :userIds")
    void deleteByUserIdIn(@Param("userIds") List<UUID> userIds);

    @Query("""
        SELECT COUNT(b) FROM PoolBooking b
        JOIN PoolSession s ON b.poolSessionId = s.id
        WHERE b.userId IN :userIds
          AND s.sessionDate BETWEEN :start AND :end
          AND b.status IN :statuses
    """)
    long countByUserIdInAndSessionDateBetweenAndStatusIn(
            @Param("userIds") List<UUID> userIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("statuses") List<PoolBookingStatus> statuses
    );
}