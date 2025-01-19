package com.mv.ams.services.monitoring;

import com.mv.ams.fixture.JobFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
class HttpAvailabilityMonitorJobTest {

    @Mock private RestTemplate client;
    @Mock private DBMonitoringResultSaver resultSaver;
    @Mock private JobExecutionContext fakeContext;

    @InjectMocks
    private HttpAvailabilityMonitorJob service;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(client, resultSaver);
    }

    @Test
    void execute_HttpRequestSuccessful_saveSUCCESSResult() {
        JobDetail jobDetail = JobFixture.jobDetail("test", 1L, HttpAvailabilityMonitorJob.class);

        Mockito.when(fakeContext.getJobDetail()).thenReturn(jobDetail);
        Mockito.when(client.getForEntity("test", String.class)).thenReturn(ResponseEntity.ok("200"));
        Mockito.doNothing().when(resultSaver).saveResult(argThat(
            actual -> actual != null
            && actual.getStatusReason() == null
            && Objects.equals(MonitoringStatus.SUCCESS, actual.getStatus()))
        );

        service.execute(fakeContext);
    }

    @Test
    void execute_HttpRequestNot2xx_saveFAILUREResult() {
        JobDetail jobDetail = JobFixture.jobDetail("test", 1L, HttpAvailabilityMonitorJob.class);

        Mockito.when(fakeContext.getJobDetail()).thenReturn(jobDetail);
        Mockito.when(client.getForEntity("test", String.class))
            .thenReturn(ResponseEntity.internalServerError().body("500"));
        Mockito.doNothing().when(resultSaver).saveResult(argThat(
			actual -> actual != null
			&& actual.getStatusReason() != null
			&& Objects.equals(MonitoringStatus.FAILURE, actual.getStatus()))
        );

        service.execute(fakeContext);
    }

    @Test
    void execute_RestException_saveFAILUREResult() {
        JobDetail jobDetail = JobFixture.jobDetail("test", 1L, HttpAvailabilityMonitorJob.class);

        Mockito.when(fakeContext.getJobDetail()).thenReturn(jobDetail);
        Mockito.when(client.getForEntity("test", String.class)).thenThrow(new RestClientException("Connection Failure"));
        Mockito.doNothing().when(resultSaver).saveResult(argThat(
            actual -> actual != null
            && actual.getStatusReason() != null
            && Objects.equals(MonitoringStatus.FAILURE, actual.getStatus()))
        );

        service.execute(fakeContext);
    }

}