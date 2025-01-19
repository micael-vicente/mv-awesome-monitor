package com.mv.ams.services.monitoring;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application.monitoring")
@Valid
public class MonitoringConfig {

    @NotNull
    private Integer httpTimeoutMillis;
}
