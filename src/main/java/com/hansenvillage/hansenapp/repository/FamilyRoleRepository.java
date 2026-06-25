package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.FamilyRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyRoleRepository extends JpaRepository<FamilyRole, Long> {
    List<FamilyRole> findByFamilyId(long familyId);
}
