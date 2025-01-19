package com.mv.ams.services.monitoring;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class MonitoringBeansConfig {

    private final MonitoringConfig config;

    @Bean
    public RestTemplate restTemplate(SimpleClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory  = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(config.getHttpTimeoutMillis());
        clientHttpRequestFactory.setReadTimeout(config.getHttpTimeoutMillis());
        return clientHttpRequestFactory;
    }


}
