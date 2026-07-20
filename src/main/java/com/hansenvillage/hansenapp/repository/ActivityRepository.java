package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

}