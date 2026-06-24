package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findById(Long id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}