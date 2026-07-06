package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.FamilyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface FamilyRoleRepository extends JpaRepository<FamilyRole, UUID> {

    Collection<FamilyRole> findByFamilyId(UUID id);
}
