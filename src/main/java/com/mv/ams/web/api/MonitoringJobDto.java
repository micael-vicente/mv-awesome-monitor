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
public class MonitoringJobDto {
    private Long id;
    private String address;
    private String cronExpression;
    private Boolean enabled;
    private String monitoringType;
    private LocalDateTime createdAt;
}
