package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolSessionRepository extends JpaRepository<PoolSession, Long> {
}
