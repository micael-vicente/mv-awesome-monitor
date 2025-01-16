package com.mv.ams.web.api;

import com.mv.ams.services.MonitoringType;
import com.mv.ams.web.validation.IsValidCronExpression;
import com.mv.ams.web.validation.IsValidURL;
import jakarta.validation.constraints.NotNull;
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
public class CreateMonitoringJobDto {

    @NotNull
    @IsValidURL
    private String address;

    @NotNull
    @IsValidCronExpression
    private String cronExpression;

    @NotNull
    private MonitoringType monitoringType;
}
