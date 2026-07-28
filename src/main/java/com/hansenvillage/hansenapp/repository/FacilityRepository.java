package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FacilityRepository extends JpaRepository<Facility, UUID> {
    Optional<Facility> findByCode(String code);

    List<Facility> findByActiveTrue();
}