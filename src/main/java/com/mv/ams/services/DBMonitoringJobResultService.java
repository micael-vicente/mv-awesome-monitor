package com.mv.ams.services;

import com.mv.ams.mapper.ServicePersistenceMapper;
import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import com.mv.ams.persistence.repository.MonitoringJobResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DBMonitoringJobResultService implements MonitoringJobResultService {

    private final MonitoringJobResultRepository resultRepository;
    private final ServicePersistenceMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Page<MonitoringJobResult> getAllJobResults(Long jobId,
                                                      LocalDateTime executedAtStart,
                                                      LocalDateTime executedAtEnd,
                                                      String status,
                                                      PageRequest pageable) {

        PageRequest sortingByExecution = pageable.withSort(Sort.Direction.DESC, "executedAt");

        Page<MonitoringJobResultEntity> results =
            resultRepository.findByFilters(jobId, executedAtStart, executedAtEnd, status, sortingByExecution);

        return results.map(mapper::map);
    }

}
