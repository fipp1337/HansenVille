package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Activity;
import com.hansenvillage.hansenapp.entity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findByType(ActivityType type);

    List<Activity> findByFamilyId(UUID familyId);
    List<Activity> findAllByOrderByDateTimeAsc();
    List<Activity> findAllByTypeOrderByDateTimeAsc(ActivityType type);
    List<Activity> findAllByFamilyIdOrderByDateTimeAsc(UUID familyId);
    List<Activity> findAllByFamilyIdAndTypeOrderByDateTimeAsc(UUID familyId, ActivityType type);
}