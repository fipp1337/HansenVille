package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PoolSessionRepository extends JpaRepository<PoolSession, UUID> {
    List<PoolSession> findBySessionDateBetween(LocalDate weekStart, LocalDate weekEnd);
    boolean existsBySessionDateAndStartTime(LocalDate sessionDate, LocalTime startTime);
}