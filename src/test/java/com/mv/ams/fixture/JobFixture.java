package com.mv.ams.fixture;

import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.services.MonitoringJob;
import com.mv.ams.services.monitoring.MonitoringType;
import com.mv.ams.services.scheduling.JobDetailsFields;
import com.mv.ams.web.api.CreateMonitoringJobDto;
import com.mv.ams.web.api.MonitoringJobDto;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

public class JobFixture {

    public static CreateMonitoringJobDto everyMinCreateJobDto(String url, boolean enabled) {
        return CreateMonitoringJobDto.builder()
                .monitoringType(MonitoringType.HTTP_AVAILABILITY.name())
                .cronExpression("0 * * ? * *")
                .address(url)
                .enabled(enabled)
                .build();
    }

    public static MonitoringJobEntity everyMinJobEntity(String url, boolean enabled) {
        MonitoringJobEntity entity = new MonitoringJobEntity();
        entity.setEnabled(enabled);
        entity.setMonitoringType(MonitoringType.HTTP_AVAILABILITY.name());
        entity.setCronExpression("0 * * ? * *");
        entity.setAddress(url);

        return entity;
    }

    public static MonitoringJobDto everyMinJobDto(String url, boolean enabled) {
        return MonitoringJobDto.builder()
                .monitoringType(MonitoringType.HTTP_AVAILABILITY.name())
                .cronExpression("0 * * ? * *")
                .address(url)
                .enabled(enabled)
                .build();
    }

    public static MonitoringJob everyMinJob(String url, boolean enabled) {
        return MonitoringJob.builder()
                .monitoringType(MonitoringType.HTTP_AVAILABILITY)
                .cronExpression("0 * * ? * *")
                .address(url)
                .enabled(enabled)
                .build();
    }

    public static JobDetail jobDetail(String url, Long jobId, Class<? extends Job> jobClass) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobDetailsFields.ADDRESS_FIELD, url);
        jobDataMap.put(JobDetailsFields.JOB_ID_FIELD, jobId);

        return JobBuilder.newJob()
                .setJobData(jobDataMap)
                .ofType(jobClass)
                .withIdentity(JobDetailsFields.JOB_KEY_PREFIX + jobId)
                .build();
    }
}
