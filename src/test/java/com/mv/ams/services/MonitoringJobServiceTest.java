package com.mv.ams.services;

import com.mv.ams.exception.EntityNotFoundException;
import com.mv.ams.exception.JobValidationException;
import com.mv.ams.fixture.JobFixture;
import com.mv.ams.mapper.ServicePersistenceMapper;
import com.mv.ams.mapper.ServicePersistenceMapperImpl;
import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.persistence.repository.MonitoringJobRepository;
import com.mv.ams.services.scheduling.MonitoringJobScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
class MonitoringJobServiceTest {

    @Mock private MonitoringJobRepository mockJobRepository;
    @Mock private MonitoringJobScheduler mockScheduler;
    private final ServicePersistenceMapper mapper = new ServicePersistenceMapperImpl();
    private MonitoringJobService service;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(mockScheduler, mockJobRepository);
        service = new MonitoringJobService(mockJobRepository, mockScheduler, mapper);
    }

    @Test
    void createJob_JobEnabledEqualsFalse_schedulerValidationSkippedAndJobPersisted() {
        MonitoringJob job = JobFixture.everyMinJob("test", false);
        MonitoringJobEntity toBeSaved = JobFixture.everyMinJobEntity("test", false);
        MonitoringJobEntity entityPersisted = JobFixture.everyMinJobEntity("test", false);
        entityPersisted.setId(1L);
        entityPersisted.setCreatedAt(LocalDateTime.now());

        Mockito.when(mockJobRepository.save(matchesMonitoringJob(toBeSaved))).thenReturn(entityPersisted);

        MonitoringJob result = service.createJob(job);

        Mockito.verifyNoInteractions(mockScheduler);
        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).save(toBeSaved);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getCreatedAt());
    }

    @Test
    void createJob_schedulerValidationFails_noInteractionsWithRepo() {
        MonitoringJob job = JobFixture.everyMinJob("test", true);

        Mockito.doThrow(JobValidationException.class).when(mockScheduler).validateCanSchedule();

        Assertions.assertThrows(JobValidationException.class, () -> service.createJob(job));

        Mockito.verifyNoInteractions(mockJobRepository);
        Mockito.verify(mockScheduler, Mockito.atMostOnce()).validateCanSchedule();
    }

    @Test
    void createJob_jobEnabledAndValid_jobScheduledAndPersisted() {
        MonitoringJob jobRequest = JobFixture.everyMinJob("test", true);
        MonitoringJob jobAfterPersistence = JobFixture.everyMinJob("test", true);
        MonitoringJobEntity toBeSaved = JobFixture.everyMinJobEntity("test", true);
        MonitoringJobEntity entityPersisted = JobFixture.everyMinJobEntity("test", true);
        entityPersisted.setId(1L);
        entityPersisted.setCreatedAt(LocalDateTime.now());
        jobAfterPersistence.setId(1L);
        jobAfterPersistence.setCreatedAt(LocalDateTime.now());

        Mockito.when(mockJobRepository.save(matchesMonitoringJob(toBeSaved))).thenReturn(entityPersisted);
        Mockito.doNothing().when(mockScheduler).validateCanSchedule();
        Mockito.doNothing().when(mockScheduler).scheduleJob(jobAfterPersistence);

        MonitoringJob result = service.createJob(jobRequest);

        Mockito.verify(mockScheduler, Mockito.atMostOnce()).validateCanSchedule();
        Mockito.verify(mockScheduler, Mockito.atMostOnce()).scheduleJob(result);
        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).save(entityPersisted);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getCreatedAt());
    }

    @Test
    void getJobById_idExists_returnJobWithId() {
        Long id = 1L;
        MonitoringJobEntity entity = JobFixture.everyMinJobEntity("test", true);
        entity.setId(id);
        entity.setCreatedAt(LocalDateTime.now());

        Mockito.when(mockJobRepository.findById(id)).thenReturn(Optional.of(entity));

        MonitoringJob result = service.getJobById(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(id, result.getId());
        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).findById(id);
    }

    @Test
    void getJobById_idDoesNotExist_propagateEntityNotFound() {
        Long id = 1L;
        Mockito.when(mockJobRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException e = Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getJobById(id));

        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).findById(id);

        Assertions.assertNotNull(e.getMessage());
        Assertions.assertTrue(e.getMessage().contains("" + id));
    }

    @Test
    void updateJobById_isEnablingDisabledJobAndSchedulerOK_scheduleJob() {
        Long id = 1L;
        UpdateMonitoringJob request = new UpdateMonitoringJob(true);
        MonitoringJobEntity existingEntity = JobFixture.everyMinJobEntity("test", false);
        existingEntity.setId(id);
        existingEntity.setCreatedAt(LocalDateTime.now().minusDays(1));

        Mockito.when(mockJobRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        Mockito.when(mockJobRepository.save(existingEntity)).thenReturn(existingEntity);
        Mockito.doNothing().when(mockScheduler).validateCanSchedule();
        Mockito.doNothing().when(mockScheduler).scheduleJob(ArgumentMatchers.any());

        MonitoringJob monitoringJob = service.updateJobById(id, request);

        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).findById(id);
        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).save(existingEntity);
        Mockito.verify(mockScheduler, Mockito.atMostOnce()).scheduleJob(ArgumentMatchers.any());

        Assertions.assertNotNull(monitoringJob);
    }

    @Test
    void updateJobById_isDisablingEnabled_schedulerDropsJob() {
        Long id = 1L;
        UpdateMonitoringJob request = new UpdateMonitoringJob(false);
        MonitoringJobEntity existingEntity = JobFixture.everyMinJobEntity("test", true);
        existingEntity.setId(id);
        existingEntity.setCreatedAt(LocalDateTime.now().minusDays(1));

        Mockito.when(mockJobRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        Mockito.when(mockJobRepository.save(existingEntity)).thenReturn(existingEntity);
        Mockito.doNothing().when(mockScheduler).dropJob(ArgumentMatchers.any());

        MonitoringJob monitoringJob = service.updateJobById(id, request);

        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).findById(id);
        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).save(existingEntity);
        Mockito.verify(mockScheduler, Mockito.atMostOnce()).dropJob(ArgumentMatchers.any());

        Assertions.assertNotNull(monitoringJob);
    }

    @Test
    void removeJob_jobIsEnabled_schedulerDropsJob() {
        Long id = 1L;
        MonitoringJobEntity existingEntity = JobFixture.everyMinJobEntity("test", true);
        existingEntity.setId(id);
        existingEntity.setCreatedAt(LocalDateTime.now().minusDays(1));

        Mockito.when(mockJobRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        Mockito.doNothing().when(mockJobRepository).deleteById(id);
        Mockito.doNothing().when(mockScheduler).dropJob(ArgumentMatchers.any());

        service.removeJob(id);

        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).findById(id);
        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).deleteById(id);
        Mockito.verify(mockScheduler, Mockito.atMostOnce()).dropJob(ArgumentMatchers.any());
    }

    @Test
    void removeJob_jobIsDisabled_schedulerNotCalled() {
        Long id = 1L;
        MonitoringJobEntity existingEntity = JobFixture.everyMinJobEntity("test", false);
        existingEntity.setId(id);
        existingEntity.setCreatedAt(LocalDateTime.now().minusDays(1));

        Mockito.when(mockJobRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        Mockito.doNothing().when(mockJobRepository).deleteById(id);

        service.removeJob(id);

        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).findById(id);
        Mockito.verify(mockJobRepository, Mockito.atMostOnce()).deleteById(id);
        Mockito.verifyNoInteractions(mockScheduler);
    }

    private MonitoringJobEntity matchesMonitoringJob(MonitoringJobEntity expected) {
        return argThat(actual -> actual != null &&
            Objects.equals(expected.getAddress(), actual.getAddress()) &&
            Objects.equals(expected.getCronExpression(), actual.getCronExpression()) &&
            Objects.equals(expected.getMonitoringType(), actual.getMonitoringType()) &&
            Objects.equals(expected.isEnabled(), actual.isEnabled())
        );
    }

}