package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.PoolTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PoolTemplateRepository extends JpaRepository<PoolTemplate, UUID> {
    List<PoolTemplate> findAllByOrderByDayOfWeekAscStartTimeAsc();
}