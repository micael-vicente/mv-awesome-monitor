package com.mv.ams.services.monitoring;

import com.mv.ams.mapper.ServicePersistenceMapper;
import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import com.mv.ams.persistence.repository.MonitoringJobRepository;
import com.mv.ams.services.MonitoringJobResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.SchedulingException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * A type of {@link MonitorJob} which saves monitoring results to
 * a database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DBMonitoringResultSaver implements MonitoringResultSaver {

    private final MonitoringJobRepository jobRepository;
    private final ServicePersistenceMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveResult(MonitoringJobResult result) {
        log.debug("Attempting to save the result to DB. Result: {}", result);
        Long jobId = result.getJobId();

        MonitoringJobEntity job = jobRepository.findById(jobId)
            .orElseThrow(() -> {
                String msg = "Result cannot be save. Broken link, job does not exist. ID: " + jobId;
                return new SchedulingException(msg);
            });

        MonitoringJobResultEntity resultEntity = mapper.map(result);
        job.addResult(resultEntity);
        jobRepository.save(job);

        log.info("Result has been persisted under job with ID: {}", jobId);
    }

}
