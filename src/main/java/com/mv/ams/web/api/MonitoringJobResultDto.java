package com.mv.ams.web.api;

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
public class MonitoringJobResultDto {
    private Long durationMillis;
    private LocalDateTime executedAt;
    private String status;
    private String statusReason;
}
