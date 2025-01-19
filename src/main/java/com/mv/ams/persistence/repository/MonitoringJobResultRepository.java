package com.mv.ams.persistence.repository;

import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MonitoringJobResultRepository extends JpaRepository<MonitoringJobResultEntity, Long> {

    @Query("""
        SELECT r FROM monitoring_job_result r
        WHERE r.job.id = :jobId
            AND (cast(:executedAtStart as date) IS NULL OR r.executedAt >= :executedAtStart)
            AND (cast(:executedAtEnd as date) IS NULL OR r.executedAt <= :executedAtEnd)
            AND (:status IS NULL OR r.status = :status)
        """)
    Page<MonitoringJobResultEntity> findByFilters(
            @Param("jobId") Long jobId,
            @Param("executedAtStart") LocalDateTime executedAtStart,
            @Param("executedAtEnd") LocalDateTime executedAtEnd,
            @Param("status") String status,
            Pageable pageable
    );
}
