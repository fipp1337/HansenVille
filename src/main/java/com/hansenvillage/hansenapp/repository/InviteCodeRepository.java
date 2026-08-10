package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.InviteCode;
import com.hansenvillage.hansenapp.entity.InviteCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, UUID> {
    Optional<InviteCode> findByCodeAndStatus(String code, InviteCodeStatus status);

    @Modifying
    @Query("UPDATE InviteCode i SET i.email = :newEmail WHERE i.email = :oldEmail")
    void updateEmailForCodes(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);
}