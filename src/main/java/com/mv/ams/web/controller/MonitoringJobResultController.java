package com.mv.ams.web.controller;

import com.mv.ams.mapper.ServiceDtoMapper;
import com.mv.ams.services.MonitoringJobResult;
import com.mv.ams.services.MonitoringJobResultService;
import com.mv.ams.web.api.MonitoringJobResultDto;
import com.mv.ams.web.api.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MonitoringJobResultController {

    private final MonitoringJobResultService resultService;
    private final ServiceDtoMapper mapper;

    /**
     * Retrieves the results of a scheduled monitoring job.
     *
     * @param id the id of the monitoring job
     * @param status the status of the results
     * @param executedAtStart since when to look for results
     * @param executedAtEnd until when to look for results
     * @param page the page wanted
     * @param size the number of elements to include in each page
     * @return results in a paginated manner, if none found empty page
     */
    @GetMapping("/jobs/{id}/results")
    public ResponseEntity<PageDto<MonitoringJobResultDto>> getAllJobResults(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "executedAtStart", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime executedAtStart,
            @RequestParam(value = "executedAtEnd", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime executedAtEnd,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Page<MonitoringJobResult> results =
                resultService.getAllJobResults(id, executedAtStart, executedAtEnd, status, PageRequest.of(page, size));
        return ResponseEntity.ok(mapper.mapResults(results));
    }
}
