package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import java.util.UUID;

@Repository
public interface PoolBookingRepository extends JpaRepository<PoolBooking, UUID> {
    boolean existsByUserIdAndPoolSessionId(UUID userId, UUID poolSessionId); // Важная штука против повторной записи
    List<PoolBooking> findByUserId(UUID id);

    @Query("SELECT pb FROM PoolBooking pb JOIN User u ON pb.userId = u.id WHERE u.familyId = :familyId")
    List<PoolBooking> findByFamilyId(@Param("familyId") UUID familyId);

    @Query("SELECT COUNT(b) FROM PoolBooking b " +
            "JOIN PoolSession s ON b.poolSessionId = s.id " +
            "JOIN User u ON b.userId = u.id " +
            "WHERE u.familyId = :familyId " +
            "AND s.sessionDate BETWEEN :start AND :end")
    long countBookingsForFamilyInWeek(
            @Param("familyId") UUID familyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    void deleteByPoolSessionId(UUID poolSessionId);

    @Query("SELECT new com.hansenvillage.hansenapp.dto.PoolBookingResponse(" +
            "b.id, b.poolSessionId, b.userId, u.name, u.age) " +
            "FROM PoolBooking b " +
            "JOIN User u ON b.userId = u.id " +
            "WHERE b.poolSessionId = :sessionId")
    List<PoolBookingResponse> findBookingDetailsBySessionId(@Param("sessionId") UUID sessionId);
}
