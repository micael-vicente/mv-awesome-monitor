package com.mv.ams.services.monitoring;

import com.mv.ams.services.MonitoringJobResult;
import com.mv.ams.services.scheduling.JobDetailsFields;
import org.quartz.JobExecutionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * A type o {@link MonitorJob} which allows monitoring availability
 * based on HTTP response status.
 * Serves as base for any other {@link }
 */
@Component
public class HttpAvailabilityMonitorJob extends HttpMonitorJob {

    private final MonitoringResultSaver resultSaver;

    public HttpAvailabilityMonitorJob(RestTemplate client, MonitoringResultSaver resultSaver) {
        super(client);
        this.resultSaver = resultSaver;
    }

    @Override
    public MonitoringType monitorType() {
        return MonitoringType.HTTP_AVAILABILITY;
    }

    @Override
    public void execute(JobExecutionContext context) {
        HttpExecutionResult executionResult = super.executeHttpGetRequest(context);
        long durationMillis = executionResult.getRequestDurationMillis();

        Long jobId = context.getJobDetail()
                .getJobDataMap()
                .getLong(JobDetailsFields.JOB_ID_FIELD);

        MonitoringJobResult result = new MonitoringJobResult();
        result.setExecutedAt(LocalDateTime.now());
        result.setJobId(jobId);
        result.setDurationMillis(durationMillis);

        if(executionResult.isFailedDueToError()) {
            result.setStatus(MonitoringStatus.FAILURE);
            result.setStatusReason(executionResult.getErrorMessage());
        } else {
            ResponseEntity<String> response = executionResult.getResponse();
            boolean success = response.getStatusCode().is2xxSuccessful();
            result.setStatus(success ? MonitoringStatus.SUCCESS : MonitoringStatus.FAILURE);
            result.setStatusReason(success ? null : "Expected status 2xx and got: " + response.getStatusCode());
        }

        resultSaver.saveResult(result);
    }
}
