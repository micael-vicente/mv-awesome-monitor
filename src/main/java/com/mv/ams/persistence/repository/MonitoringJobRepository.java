package com.mv.ams.persistence.repository;

import com.mv.ams.persistence.model.MonitoringJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringJobRepository extends JpaRepository<MonitoringJobEntity, Long> {
}
