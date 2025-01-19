package com.mv.ams.services;

import com.mv.ams.exception.EntityNotFoundException;
import com.mv.ams.mapper.ServicePersistenceMapper;
import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.persistence.repository.MonitoringJobRepository;
import com.mv.ams.services.scheduling.MonitoringJobScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringJobService {

    private final MonitoringJobRepository jobRepository;
    private final MonitoringJobScheduler scheduler;
    private final ServicePersistenceMapper mapper;

    /**
     * Validates if the scheduler accepts new jobs and,
     * if so, persists it and schedules it.
     * <p>
     * If the job is to be created with <code>enabled == false</code>
     * skips the scheduler validation but the job will not run it is
     * updated to be enabled.
     * </p>
     * @param job the configuration of the job to schedule
     * @return the job after being persisted
     */
    @Transactional
    public MonitoringJob createJob(MonitoringJob job) {
        log.debug("Attempting to create a new job: {}", job);
        validateIfScheduling(job.isEnabled());

        MonitoringJobEntity persistedJob = jobRepository.save(mapper.map(job));
        log.info("Job successfully persisted with ID: {}", persistedJob.getId());

        MonitoringJob mappedToService = mapper.map(persistedJob);

        if(job.isEnabled()) {
            log.info("Going to schedule job with id: {}", mappedToService.getId());
            scheduler.scheduleJob(mappedToService);
        }

        return mappedToService;
    }

    /**
     * Fetches a job configuration given its id.
     *
     * @param id the id of the job to fetch
     * @return the configuration if found
     * @throws EntityNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public MonitoringJob getJobById(Long id) {
        log.debug("Fetching job with ID: {}", id);
        return jobRepository.findById(id)
            .map(mapper::map)
            .orElseThrow(() -> new EntityNotFoundException("MonitoringJob", id));
    }

    /**
     * Updates an existing job configuration by replacing in the persisted job
     * the fields provided through {@link UpdateMonitoringJob}.
     * @param id the id of the job to update
     * @param update the fields to be updated
     * @return the job after merging and persisting changes
     */
    @Transactional
    public MonitoringJob updateJobById(Long id, UpdateMonitoringJob update) {
        log.debug("Updating job with ID: {}, update details: {}", id, update);
        MonitoringJobEntity existing = jobRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("MonitoringJob", id));
        boolean previouslyEnabled = existing.isEnabled();

        mapper.update(update, existing);
        MonitoringJobEntity result = jobRepository.save(existing);

        if(shouldEnable(previouslyEnabled, update.isEnabled())) {
            log.info("Enabling and scheduling job with ID: {}", id);
            scheduler.validateCanSchedule();
            scheduler.scheduleJob(mapper.map(result));
        }

        if(shouldDisable(previouslyEnabled, update.isEnabled())) {
            log.info("Disabling and dropping job with ID: {}", id);
            scheduler.dropJob(mapper.map(result));
        }

        return mapper.map(result);
    }

    /**
     * Gets all configured jobs in a paginated manner.
     * @param pageable the pagination details
     * @return results according to pagination
     */
    @Transactional(readOnly = true)
    public Page<MonitoringJob> getAll(PageRequest pageable) {
        PageRequest withSort = pageable.withSort(Sort.Direction.ASC, "createdAt");
        return jobRepository.findAll(withSort)
            .map(mapper::map);
    }

    /**
     * Hard remove of a job of given id. Does nothing if the job does
     * not exist.
     * @param id the id of the job to be removed
     */
    @Transactional
    public void removeJob(Long id) {
        log.debug("Attempting to remove job with ID: {}", id);
        boolean shouldDropJob = jobRepository.findById(id)
            .map(MonitoringJobEntity::isEnabled)
            .orElse(false);

        if(shouldDropJob) {
            log.info("Dropping scheduled job with ID: {} before removal", id);
            scheduler.dropJob(MonitoringJob.builder().id(id).build());
        }

        jobRepository.deleteById(id);
        log.info("Job with ID: {} removed successfully", id);
    }

    private boolean shouldDisable(boolean existing, boolean other) {
        return !shouldEnable(existing, other);
    }

    private boolean shouldEnable(boolean existing, boolean other) {
        return !existing && other;
    }

    private void validateIfScheduling(boolean tryingToSchedule) {
        if(tryingToSchedule) {
            log.debug("Validating if new jobs can be scheduled");
            scheduler.validateCanSchedule();
        }
    }

}
