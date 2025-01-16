package com.mv.ams.services.monitoring;

import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import com.mv.ams.persistence.repository.MonitoringJobResultRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseMonitorJob implements MonitorJob {

    private final MonitoringJobResultRepository repository;

    void registerResult(MonitoringJobResultEntity entity) {
        repository.save(entity);
    }

}
