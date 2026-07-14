package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.dto.CinemaSeatResponse;
import com.hansenvillage.hansenapp.entity.CinemaSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CinemaSeatRepository extends JpaRepository<CinemaSeat, UUID> {

    List<CinemaSeat> findByHallId(UUID hallId);
}
