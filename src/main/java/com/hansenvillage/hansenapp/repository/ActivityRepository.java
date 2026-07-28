package com.hansenvillage.hansenapp.repository;

import com.hansenvillage.hansenapp.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

}