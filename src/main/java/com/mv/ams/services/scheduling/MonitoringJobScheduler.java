package com.mv.ams.services.scheduling;

import com.mv.ams.exception.JobSchedulingException;
import com.mv.ams.exception.JobValidationException;
import com.mv.ams.services.MonitoringJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringJobScheduler {

    private final Scheduler scheduler;
    private final QuartzServiceMapper mapper;
    private final SchedulingConfig config;

    /**
     * Schedules a new monitoring job using the configuration provided
     * via {@link MonitoringJob}.
     * <p>
     * Currently, supports only cron based triggering.
     * Cron expression is to be provided through {@link MonitoringJob#getCronExpression()}.
     * </p>
     * <p>
     * Uses {@link MonitoringJob#getId()} as part of its unique identifier.
     * </p>
     * <p>
     * The type of the job is defined using {@link MonitoringJob#getMonitoringType()}
     * </p>
     * @param job contains all variables used to schedule the new job
     * @throws JobSchedulingException if scheduling was not possible
     * due to something unexpected
     */
    public void scheduleJob(MonitoringJob job) {
        try {
            JobDetail jobDetail = mapper.toJobDetail(job);
            Trigger trigger = mapper.toTrigger(jobDetail, job.getCronExpression());
            scheduler.addJob(jobDetail, true, true);
            scheduler.scheduleJob(trigger);
        } catch (SchedulerException e) {
            String message = "Failed to schedule job with id: " + job.getId();
            throw new JobSchedulingException(message, e);
        }
    }

    /**
     * Drops a job from the scheduler, it will no longer be executed.
     * <p>
     * Uses {@link MonitoringJob#getId()} to formulate the job id.
     * </p>
     * @param job the job to be dropped
     * @throws JobSchedulingException if dropping the job was not possible
     * due to something unexpected
     */
    public void dropJob(MonitoringJob job) {
        try {
            JobKey jobKey = mapper.toJobKey(job);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            String message = "Failed to drop job with id: " + job.getId();
            throw new JobSchedulingException(message, e);
        }
    }

    /**
     * Validates whether the scheduler is in condition to accept new jobs.
     * <p>
     * Current criteria includes checking if the number of active jobs is under
     * the value configured through <code>application.monitoring.max-jobs</code>.
     * </p>
     * @throws JobSchedulingException if no more jobs can be scheduled
     * @throws JobSchedulingException if something unexpected prevents the access
     * to the scheduler
     */
    public void validateCanSchedule() {
        try {
            Set<JobKey> jobs = scheduler.getJobKeys(GroupMatcher.anyGroup());
            if(jobs.size() >= config.getMaxJobs()) {
                throw new JobValidationException("Cannot schedule new jobs. Max jobs: " + config.getMaxJobs());
            }
        } catch (SchedulerException e) {
            String message = "Failed to count active jobs";
            throw new JobSchedulingException(message, e);
        }
    }

}
