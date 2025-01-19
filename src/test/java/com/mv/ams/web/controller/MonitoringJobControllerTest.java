package com.mv.ams.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.ams.exception.EntityNotFoundException;
import com.mv.ams.exception.JobSchedulingException;
import com.mv.ams.exception.JobValidationException;
import com.mv.ams.fixture.JobFixture;
import com.mv.ams.mapper.ServiceDtoMapper;
import com.mv.ams.services.MonitoringJob;
import com.mv.ams.services.MonitoringJobService;
import com.mv.ams.services.UpdateMonitoringJob;
import com.mv.ams.web.api.CreateMonitoringJobDto;
import com.mv.ams.web.api.MonitoringJobDto;
import com.mv.ams.web.api.UpdateMonitoringJobDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MonitoringJobController.class)
class MonitoringJobControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MonitoringJobService service;

    @MockitoBean
    private ServiceDtoMapper mapper;

    @Test
    @SneakyThrows
    void createJob_missingRequiredParameters_errorResponse400With3FieldsError() {
        CreateMonitoringJobDto request = new CreateMonitoringJobDto();
        request.setEnabled(true);

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists())
            .andExpect(jsonPath("$.errors.cronExpression").exists())
            .andExpect(jsonPath("$.errors.monitoringType").exists())
            .andExpect(jsonPath("$.errors.address").exists());
    }

    @Test
    @SneakyThrows
    void createJob_cronExpressionNotValid_errorResponse400WithCronExpressionError() {
        CreateMonitoringJobDto request = new CreateMonitoringJobDto();
        request.setEnabled(true);
        request.setAddress("http://test");
        request.setCronExpression("* * * * * *");
        request.setMonitoringType("HTTP_AVAILABILITY");

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists())
            .andExpect(jsonPath("$.errors.cronExpression").exists())
            .andExpect(jsonPath("$.errors.cronExpression")
                .value(equalTo("Provided cron expression is not valid")));
    }

    @Test
    @SneakyThrows
    void createJob_addressNotValid_errorResponse400WithAddressError() {
        CreateMonitoringJobDto request = new CreateMonitoringJobDto();
        request.setEnabled(true);
        request.setAddress("http:/\test");
        request.setCronExpression("* * * ? * *");
        request.setMonitoringType("HTTP_AVAILABILITY");

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists())
            .andExpect(jsonPath("$.errors.address").exists())
            .andExpect(jsonPath("$.errors.address")
                .value(equalTo("Address provided is not a valid URL")));
    }

    @Test
    @SneakyThrows
    void createJob_monitoringTypeNotInEnum_errorResponse400WithMonitoringTypeError() {
        CreateMonitoringJobDto request = new CreateMonitoringJobDto();
        request.setEnabled(true);
        request.setAddress("http://test");
        request.setCronExpression("* * * ? * *");
        request.setMonitoringType("HTTP_AVAILABILITY1");

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists())
            .andExpect(jsonPath("$.errors.monitoringType").exists())
            .andExpect(jsonPath("$.errors.monitoringType")
                .value(startsWith("The value provided is not one of:")));
    }

    @Test
    @SneakyThrows
    void createJob_maxJobsExceeded_errorResponse400WithMessage() {
        String url = "http://test";
        CreateMonitoringJobDto request = JobFixture.everyMinCreateJobDto(url, true);
        MonitoringJob monitoringJob = JobFixture.everyMinJob(url, true);

        when(mapper.map(request)).thenReturn(monitoringJob);
        when(service.createJob(monitoringJob)).thenThrow(new JobValidationException("Max jobs exceeded"));

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").exists())
            .andExpect(jsonPath("$.detail")
                .value(equalTo("Max jobs exceeded")));
    }

    @Test
    @SneakyThrows
    void createJob_schedulingFailed_errorResponse400WithMessage() {
        String url = "http://test";
        CreateMonitoringJobDto request = JobFixture.everyMinCreateJobDto(url, true);
        MonitoringJob monitoringJob = JobFixture.everyMinJob(url, true);

        when(mapper.map(request)).thenReturn(monitoringJob);
        when(service.createJob(monitoringJob)).thenThrow(new JobSchedulingException("Failed to schedule job"));

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").exists())
            .andExpect(jsonPath("$.detail")
                .value(equalTo("Failed to schedule job")));
    }

    @Test
    @SneakyThrows
    void createJob_unknownRuntime_errorResponse500WithNoDetailsOfError() {
        String url = "http://test";
        CreateMonitoringJobDto request = JobFixture.everyMinCreateJobDto(url, true);
        MonitoringJob monitoringJob = JobFixture.everyMinJob(url, true);

        when(mapper.map(request)).thenReturn(monitoringJob);
        when(service.createJob(monitoringJob)).thenThrow(new RuntimeException("Super error with stacktrace"));

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.detail").exists())
            .andExpect(jsonPath("$.detail")
                .value(not(equalTo("Super error with stacktrace"))));
    }

    @Test
    @SneakyThrows
    void createJob_success_monitoringJobDetails() {
        String url = "http://test";
        CreateMonitoringJobDto request = JobFixture.everyMinCreateJobDto(url, true);
        MonitoringJob requestMapped = JobFixture.everyMinJob(url, true);
        MonitoringJob result = JobFixture.everyMinJob(url, true);
        result.setId(1L);
        MonitoringJobDto resultMapped = JobFixture.everyMinJobDto(url, true);
        resultMapped.setId(1L);

        when(mapper.map(request)).thenReturn(requestMapped);
        when(service.createJob(requestMapped)).thenReturn(result);
        when(mapper.map(result)).thenReturn(resultMapped);

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @SneakyThrows
    void updateJob_entityNotFound_400response() {
        long id = 1L;
        UpdateMonitoringJobDto request = UpdateMonitoringJobDto.builder().enabled(true).build();
        UpdateMonitoringJob requestMapped = UpdateMonitoringJob.builder().enabled(true).build();

        when(mapper.map(request)).thenReturn(requestMapped);
        when(service.updateJobById(id, requestMapped)).thenThrow(EntityNotFoundException.class);

        String body = objectMapper.writeValueAsString(request);
        mvc.perform(put("/api/v1/jobs/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

}
