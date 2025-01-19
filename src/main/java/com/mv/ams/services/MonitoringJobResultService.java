package com.mv.ams.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

public interface MonitoringJobResultService {
    Page<MonitoringJobResult> getAllJobResults(Long jobId,
                                               LocalDateTime executedAtStart,
                                               LocalDateTime executedAtEnd,
                                               String result,
                                               PageRequest pageable);
}
