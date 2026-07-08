package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {
    Optional<Family> findByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying
    @Query("""
            UPDATE Family f
            SET f.memberCount = f.memberCount + 1
            WHERE f.id = :familyId
            """)
    Integer incrementMemberCount(UUID familyId);

    @Modifying
    @Query("""
            UPDATE Family f
            SET f.memberCount = f.memberCount - 1
            WHERE f.id = :familyId
            """)
    Integer decrementMemberCount(UUID familyId);

    void deleteByEmail(String email);

    void deleteByAddress(String address);
}