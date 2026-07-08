package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolBooking;
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

    boolean existsByUserIdAndPoolSessionId(UUID userId, UUID poolSessionId);

    @Query("SELECT COUNT(b) FROM PoolBooking b " +
            "JOIN PoolSession s ON b.poolSessionId = s.id " +
            "JOIN User u ON b.userId = u.id " +
            "WHERE u.familyId = :familyId " +
            "AND s.sessionDate BETWEEN :start AND :end " +
            "AND b.status IN ('REGISTERED', 'CANCELED_WITHOUT_RETURN')")
    long countBookingsForFamilyInWeek(
            @Param("familyId") UUID familyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


    Optional<PoolBooking> findByUserIdAndPoolSessionId(UUID userId, UUID poolSessionId);

    List<PoolBooking> findByPoolSessionId(UUID poolSessionId);

    @Modifying
    @Query("DELETE FROM PoolBooking b WHERE b.poolSessionId = :poolSessionId")
    void deleteByPoolSessionId(@Param("poolSessionId") UUID poolSessionId);

    @Query("""
        SELECT new com.hansenvillage.hansenapp.dto.PoolBookingResponse(
            b.id, b.poolSessionId, b.userId, u.name, u.age, b.status, s.sessionDate, s.startTime, s.endTime
        )
        FROM PoolBooking b
        JOIN User u ON b.userId = u.id
        JOIN PoolSession s ON b.poolSessionId = s.id
        WHERE b.poolSessionId = :sessionId
    """)
    List<PoolBookingResponse> findBookingDetailsBySessionId(@Param("sessionId") UUID sessionId);

    @Query("""
        SELECT new com.hansenvillage.hansenapp.dto.PoolBookingResponse(
            b.id, b.poolSessionId, b.userId, u.name, u.age, b.status, s.sessionDate, s.startTime, s.endTime
        )
        FROM PoolBooking b
        JOIN User u ON b.userId = u.id
        JOIN PoolSession s ON b.poolSessionId = s.id
        WHERE u.familyId = :familyId
          AND b.status = 'REGISTERED'
          AND (s.sessionDate > CURRENT_DATE OR (s.sessionDate = CURRENT_DATE AND s.startTime > CURRENT_TIME))
    """)
    List<PoolBookingResponse> findFutureRegisteredByFamilyId(@Param("familyId") UUID familyId);

    @Query("""
        SELECT new com.hansenvillage.hansenapp.dto.PoolBookingResponse(
            b.id, b.poolSessionId, b.userId, u.name, u.age, b.status, s.sessionDate, s.startTime, s.endTime
        )
        FROM PoolBooking b
        JOIN User u ON b.userId = u.id
        JOIN PoolSession s ON b.poolSessionId = s.id
        WHERE b.userId = :userId
          AND b.status = 'REGISTERED'
          AND (s.sessionDate > CURRENT_DATE OR (s.sessionDate = CURRENT_DATE AND s.startTime > CURRENT_TIME))
    """)
    List<PoolBookingResponse> findFutureRegisteredByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM PoolBooking b WHERE b.userId IN :userIds")
    void deleteByUserIdIn(@Param("userIds") List<UUID> userIds);
}