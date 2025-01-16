package com.mv.ams.services;

import com.mv.ams.config.AppConfiguration;
import com.mv.ams.mapper.ServicePersistenceMapper;
import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.persistence.repository.MonitoringJobRepository;
import com.mv.ams.services.scheduling.MonitoringJobScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringJobService {

    private final MonitoringJobRepository repository;
    private final MonitoringJobScheduler scheduler;
    private final ServicePersistenceMapper mapper;
    private final AppConfiguration config;

    @Transactional
    public MonitoringJob createJob(MonitoringJob job) {
        validateNotBeingMonitored(job);
        validateNotTooFrequent(job);

        MonitoringJobEntity persisted = repository.save(mapper.map(job));

        scheduler.scheduleTask(job);

        return mapper.map(persisted);
    }

    @Transactional(readOnly = true)
    public MonitoringJob getJobById(Long id) {
        return repository.findById(id)
            .map(mapper::map)
            .orElseThrow();
    }

    @Transactional(readOnly = true)
    public Page<MonitoringJob> getAll(PageRequest pageable) {
        return repository.findAll(pageable)
            .map(mapper::map);
    }

    @Transactional
    public void removeJob(Long id) {
        repository.deleteById(id);
    }

    private void validateNotTooFrequent(MonitoringJob job) {
        boolean tooFrequent;
        try {
            tooFrequent = scheduler.isTooFrequent(job.getCronExpression(), config.getMonitoring().getCron().getMinIntervalSeconds());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if(tooFrequent) {
            throw new RuntimeException("Please reduce the monitoring frequency");
        }
    }

    private void validateNotBeingMonitored(MonitoringJob job) {
        boolean alreadyMonitoring = repository.existsByShortAddress(job.getShortAddress());

        if(alreadyMonitoring) {
            throw new RuntimeException("Address already being monitored");
        }
    }

}
