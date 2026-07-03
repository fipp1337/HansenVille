package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.CinemaSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CinemaSeatRepository extends JpaRepository<CinemaSeat, UUID> {

}
