package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.FamiliesUsers;
import com.hansenvillage.hansenapp.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamiliesUsersRepository extends JpaRepository<FamiliesUsers, Long> {

}
