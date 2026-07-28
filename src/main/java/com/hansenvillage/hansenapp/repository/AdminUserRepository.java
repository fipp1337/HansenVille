package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {
    boolean existsByEmail(String email);
    Optional<AdminUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

}
