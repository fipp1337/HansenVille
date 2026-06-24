package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolBook;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolBookRepository extends JpaRepository<PoolBook, Long> {
    boolean existsByUserAndPoolSession(User user, PoolSession poolSession); // Важная штука против повторной записи
}
