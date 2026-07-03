package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.CinemaSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CinemaSessionRepository extends JpaRepository<CinemaSession, UUID> {

    List<CinemaSession> findBySessionDateBetween(LocalDate weekStart, LocalDate weekEnd);

    boolean existsBySessionDateAndStartTime(LocalDate sessionDate, LocalTime startTime);
}