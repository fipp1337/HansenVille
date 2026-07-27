package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PoolSessionRepository extends JpaRepository<PoolSession, UUID> {
    List<PoolSession> findBySessionDateBetween(LocalDate weekStart, LocalDate weekEnd);
    boolean existsBySessionDateAndStartTime(LocalDate sessionDate, LocalTime startTime);

    @Query("SELECT s.id FROM PoolSession s WHERE s.sessionDate = :date")
    List<UUID> findIdsByDate(@Param("date") LocalDate date);

}
