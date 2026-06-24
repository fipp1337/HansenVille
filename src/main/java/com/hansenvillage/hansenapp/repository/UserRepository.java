package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    int countByFamilyId(Long familyId);
    List<User> findByFamilyId(Long familyId);
}
