package com.mv.ams.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringJobResult {
    private Long id;
    private Long jobId;
    private LocalDateTime executedAt;
    private Long durationMillis;
    private String status;
    private String statusReason;
}
