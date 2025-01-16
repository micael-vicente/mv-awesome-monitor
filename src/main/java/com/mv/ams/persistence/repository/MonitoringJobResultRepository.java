package com.mv.ams.persistence.repository;

import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoringJobResultRepository extends JpaRepository<MonitoringJobResultEntity, Long> {
}
