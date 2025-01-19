package com.mv.ams.services;

import com.mv.ams.services.monitoring.MonitoringType;
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
public class MonitoringJob {
    private Long id;
    private String address;
    private String cronExpression;
    private boolean enabled;
    private MonitoringType monitoringType;
    private LocalDateTime createdAt;
}
