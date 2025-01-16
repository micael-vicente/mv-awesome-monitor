package com.mv.ams.web.controller;

import com.mv.ams.mapper.ServiceDtoMapper;
import com.mv.ams.services.MonitoringJob;
import com.mv.ams.services.MonitoringJobService;
import com.mv.ams.web.api.CreateMonitoringJobDto;
import com.mv.ams.web.api.MonitoringJobDto;
import com.mv.ams.web.api.PageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringJobService service;
    private final ServiceDtoMapper mapper;

    @PostMapping(value = "/jobs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonitoringJobDto> createJob(@Valid @RequestBody CreateMonitoringJobDto requestBody) {
        MonitoringJob job = service.createJob(mapper.map(requestBody));
        return ResponseEntity.ok(mapper.map(job));
    }

    @GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDto<MonitoringJobDto>> getAllJobs(
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<MonitoringJob> response = service.getAll(PageRequest.of(page, size));
        return ResponseEntity.ok(mapper.map(response));
    }

    @GetMapping(value = "/jobs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MonitoringJobDto> getJob(@PathVariable(value = "id") Long id) {
        MonitoringJob job = service.getJobById(id);
        return ResponseEntity.ok(mapper.map(job));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> removeJob(@PathVariable(value = "id") Long id) {
        service.removeJob(id);
        return ResponseEntity.noContent()
            .build();
    }

}
