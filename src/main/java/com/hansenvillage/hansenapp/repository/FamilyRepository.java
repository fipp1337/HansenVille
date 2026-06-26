package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
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
    update Family f
    set f.memberCount = f.memberCount - 1
    where f.id = :id
""")
    int decrementMemberCount(UUID id);

    @Modifying
    @Query("""
    update Family f
    set f.memberCount = f.memberCount + 1
    where f.id = :id
""")
    int incrementMemberCount(UUID id);
}