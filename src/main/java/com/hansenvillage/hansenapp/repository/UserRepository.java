package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    int countByFamilyId(UUID familyId);
    List<User> findByFamilyId(UUID familyId);
    void deleteByFamilyId(UUID familyId);
}
