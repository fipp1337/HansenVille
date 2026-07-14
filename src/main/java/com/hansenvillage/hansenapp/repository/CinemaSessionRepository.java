package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.CinemaSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CinemaSessionRepository extends JpaRepository<CinemaSession, UUID> {

    List<CinemaSession> findByStartAtBetween(LocalDateTime from, LocalDateTime to);

    boolean existsByStartAt(LocalDateTime startAt);
}