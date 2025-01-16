package com.mv.ams.services.monitoring;

import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import com.mv.ams.persistence.repository.MonitoringJobResultRepository;
import com.mv.ams.services.MonitoringType;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpAvailabilityMonitorJob extends BaseMonitorJob {

    private final RestTemplate client;

    public HttpAvailabilityMonitorJob(MonitoringJobResultRepository repository, RestTemplate client) {
        super(repository);
        this.client = client;
    }

    @Override
    public MonitoringType monitorType() {
        return MonitoringType.HTTP_AVAILABILITY;
    }

    private String status(HttpStatusCode statusCode) {
        if(statusCode.is2xxSuccessful()) {
            return "SUCCESS";
        } else {
            return "ERROR";
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String address = context.getJobDetail().getJobDataMap().getString("address");

        ResponseEntity<String> response = client.getForEntity(address, String.class);
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();

        MonitoringJobResultEntity result = new MonitoringJobResultEntity();
        result.setResult(status(response.getStatusCode()));
        result.setDurationMillis(totalTimeMillis);

        super.registerResult(result);
    }
}
