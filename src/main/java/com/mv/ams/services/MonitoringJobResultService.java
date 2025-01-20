package com.mv.ams.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

public interface MonitoringJobResultService {

    /**
     * Fetches results from their persistence and allows
     * filtering of said results by several criteria.
     * @param jobId the id of the monitoring job the results belong to
     * @param executedAtStart results generated starting at
     * @param executedAtEnd results generated ending at
     * @param status the result status
     * @param pageable the pagination parameters
     * @return a page with found results or an empty page if none found
     */
    Page<MonitoringJobResult> getAllJobResults(Long jobId,
                                               LocalDateTime executedAtStart,
                                               LocalDateTime executedAtEnd,
                                               String status,
                                               PageRequest pageable);
}
