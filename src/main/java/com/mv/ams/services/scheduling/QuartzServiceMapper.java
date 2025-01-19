package com.mv.ams.services.scheduling;

import com.mv.ams.services.MonitoringJob;
import com.mv.ams.services.monitoring.MonitoringRouter;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuartzServiceMapper {

    private final MonitoringRouter router;

    /**
     * Initializes and returns a new {@link Trigger} with a Cron schedule.
     * <p>
     * Configured to do nothing when there are misfires.
     * </p>
     * @param jobDetail details of the job being triggered
     * @param cron the cron expression to be used
     * @return a newly configured cron trigger
     */
    public Trigger toTrigger(JobDetail jobDetail, String cron) {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(CronScheduleBuilder.cronSchedule(cron)
                .withMisfireHandlingInstructionDoNothing())
            .build();
    }

    /**
     * Maps a {@link MonitoringJob} to a {@link JobDetail}.
     * @param job object holding the configuration required
     * @return the object ready to being used
     */
    public JobDetail toJobDetail(MonitoringJob job) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobDetailsFields.ADDRESS_FIELD, job.getAddress());
        jobDataMap.put(JobDetailsFields.JOB_ID_FIELD, job.getId());

        return JobBuilder.newJob()
            .setJobData(jobDataMap)
            .ofType(router.getMonitor(job.getMonitoringType()))
            .withIdentity(JobDetailsFields.JOB_KEY_PREFIX + job.getId())
            .build();
    }

    /**
     * Maps {@link MonitoringJob} to a {@link JobKey}.
     * JobKey identifies a job scheduled.
     * @param job the configuration of a job
     * @return the key a configuration will have
     */
    public JobKey toJobKey(MonitoringJob job) {
        return JobKey.jobKey(JobDetailsFields.JOB_KEY_PREFIX + job.getId());
    }
}
