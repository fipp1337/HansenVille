package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.InviteCode;
import com.hansenvillage.hansenapp.entity.InviteCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, UUID> {
    Optional<InviteCode> findByCodeAndStatus(String code, InviteCodeStatus status);
}