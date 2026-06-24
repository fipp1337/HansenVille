package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PoolSessionRepository extends JpaRepository<PoolSession, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PoolSession> findById(Long id);
    List<PoolSession> findBySessionDateBetweenAndIsExclusiveFalseAndStatus(LocalDate startDate, LocalDate endDate, String status);
}
