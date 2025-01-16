package com.mv.ams.services.scheduling;

import com.mv.ams.services.MonitoringJob;
import com.mv.ams.services.monitoring.MonitoringRouter;
import lombok.RequiredArgsConstructor;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MonitoringJobScheduler {

    private final Scheduler scheduler;
    private final MonitoringRouter router;

    public void scheduleTask(MonitoringJob job) {
        try {
            JobDetail jobDetail = getJobDetail(job);
            Trigger trigger = triggerJob(jobDetail, job.getCronExpression());
            scheduler.addJob(jobDetail, true, true);
            scheduler.scheduleJob(trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private Trigger triggerJob(JobDetail jobDetail, String cron) {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build();
    }

    private JobDetail getJobDetail(MonitoringJob job) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("address", job.getAddress());

        return JobBuilder.newJob()
            .setJobData(jobDataMap)
            .ofType(router.getMonitor(job.getMonitoringType()))
            .withIdentity("MonitoringJob-" + job.getId())
            .build();
    }

    public boolean isTooFrequent(String cronExpression, long minFreqInSec) throws ParseException {
        Date now = new Date();
        CronExpression cron = new CronExpression(cronExpression);

        Date nextTrigger = cron.getNextValidTimeAfter(now);

        if(nextTrigger == null) {
            //past trigger?
            return false;
        }

        Date subsequentTrigger = cron.getNextValidTimeAfter(nextTrigger);

        if(subsequentTrigger == null) {
            //single trigger
            return true;
        }

        long intervalMillis = subsequentTrigger.getTime() - nextTrigger.getTime();

        return intervalMillis < TimeUnit.SECONDS.toMillis(minFreqInSec);
    }
}
