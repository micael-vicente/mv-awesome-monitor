package com.mv.ams.web.api;

import com.mv.ams.services.MonitoringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringJobDto {
    private Long id;
    private String address;
    private String cronExpression;
    private MonitoringType monitoringType;
}
