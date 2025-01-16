package com.mv.ams.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
public class AppConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private Monitoring monitoring;

    @Data
    public static class Monitoring {
        private Cron cron;
    }

    @Data
    public static class Cron {
        private long minIntervalSeconds;
    }
}
