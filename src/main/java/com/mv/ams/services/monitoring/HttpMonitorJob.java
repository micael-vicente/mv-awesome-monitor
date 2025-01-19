package com.mv.ams.services.monitoring;

import com.mv.ams.services.scheduling.JobDetailsFields;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public abstract class HttpMonitorJob implements MonitorJob {

    private final RestTemplate client;

    /**
     * Executes an HTTP GET request using the address field inside the job context.
     * Saves the result of the call to a {@link HttpExecutionResult} object.
     * <p>
     * The content of the response is of type String.
     * </p>
     * <p>
     * Also monitors the duration of the call.
     * </p>
     * @param context the context of the job execution
     * @return the HttpExecutionResult containing the response entity and duration in millis
     */
    protected HttpExecutionResult executeHttpGetRequest(JobExecutionContext context) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String url = context.getJobDetail()
                .getJobDataMap()
                .getString(JobDetailsFields.ADDRESS_FIELD);

        HttpExecutionResult executionResult = new HttpExecutionResult();
        try {
            ResponseEntity<String> response = client.getForEntity(url, String.class);
            executionResult.setResponse(response);
        } catch (RestClientException e) {
            executionResult.setFailedDueToError(true);
            executionResult.setErrorMessage(e.getMessage());
        } finally {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            executionResult.setRequestDurationMillis(totalTimeMillis);
        }

        return executionResult;
    }
}
