package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.PoolTemplate;
import com.hansenvillage.hansenapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PoolTemplateRepository extends JpaRepository<PoolTemplate, Long> {

}