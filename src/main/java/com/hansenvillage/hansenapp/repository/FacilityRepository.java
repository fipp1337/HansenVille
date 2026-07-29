package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
    Optional<Facility> findByCode(String code);

    List<Facility> findByActiveTrue();
}