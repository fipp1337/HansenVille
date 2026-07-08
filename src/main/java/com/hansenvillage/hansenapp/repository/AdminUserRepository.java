package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {
    boolean existsByEmail(String email);
    Optional<AdminUser> findByEmail(String email);
}
