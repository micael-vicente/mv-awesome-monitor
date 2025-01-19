package com.mv.ams.services.scheduling;

import com.mv.ams.exception.JobSchedulingException;
import com.mv.ams.exception.JobValidationException;
import com.mv.ams.fixture.JobFixture;
import com.mv.ams.services.MonitoringJob;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class MonitoringJobSchedulerTest {

    @Mock private Scheduler scheduler;
    @Mock private QuartzServiceMapper mapper;
    @Mock private SchedulingConfig config;

    @InjectMocks
    private MonitoringJobScheduler service;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(scheduler, mapper, config);
    }

    @Test
    @SneakyThrows
    void scheduleJob_schedulerException_jobSchedulingExceptionPropagated() {
        MonitoringJob job = JobFixture.everyMinJob("test", true);
        JobDetail jobDetails = new JobDetailImpl();
        Trigger trigger = new CronTriggerImpl();

        Mockito.when(mapper.toJobDetail(job)).thenReturn(jobDetails);
        Mockito.when(mapper.toTrigger(jobDetails, job.getCronExpression())).thenReturn(trigger);
        Mockito.doThrow(SchedulerException.class).when(scheduler).addJob(jobDetails, true, true);

        Assertions.assertThrows(JobSchedulingException.class, () -> service.scheduleJob(job));
    }

    @Test
    @SneakyThrows
    void dropJob_schedulerException_jobSchedulingExceptionPropagated() {
        MonitoringJob job = JobFixture.everyMinJob("test", true);
        JobKey jobKey = new JobKey("test");

        Mockito.when(mapper.toJobKey(job)).thenReturn(jobKey);
        Mockito.doThrow(SchedulerException.class).when(scheduler).deleteJob(jobKey);

        Assertions.assertThrows(JobSchedulingException.class, () -> service.dropJob(job));
    }

    @Test
    @SneakyThrows
    void validateCanSchedule_schedulerException_jobSchedulingExceptionPropagated() {
        Mockito.when(scheduler.getJobKeys(GroupMatcher.anyGroup())).thenThrow(SchedulerException.class);

        Assertions.assertThrows(JobSchedulingException.class, () -> service.validateCanSchedule());
    }

    @Test
    @SneakyThrows
    void validateCanSchedule_validationFails_jobValidationExceptionPropagated() {
        JobKey key1 = new JobKey("1");
        JobKey key2 = new JobKey("2");
        Mockito.when(scheduler.getJobKeys(GroupMatcher.anyGroup())).thenReturn(Set.of(key1, key2));
        Mockito.when(config.getMaxJobs()).thenReturn(1);

        Assertions.assertThrows(JobValidationException.class, () -> service.validateCanSchedule());
    }

    @Test
    @SneakyThrows
    void validateCanSchedule_validationOk_noExceptionsThrown() {
        JobKey key1 = new JobKey("1");
        JobKey key2 = new JobKey("2");
        Mockito.when(scheduler.getJobKeys(GroupMatcher.anyGroup())).thenReturn(Set.of(key1, key2));
        Mockito.when(config.getMaxJobs()).thenReturn(3);

        Assertions.assertDoesNotThrow(() -> service.validateCanSchedule());
    }
}