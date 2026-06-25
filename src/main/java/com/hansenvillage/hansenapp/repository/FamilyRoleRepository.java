package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.FamilyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRoleRepository extends JpaRepository<FamilyRole, Long> {


}
