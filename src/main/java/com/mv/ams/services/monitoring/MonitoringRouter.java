package com.mv.ams.services.monitoring;

import com.mv.ams.services.MonitoringType;
import org.quartz.Job;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MonitoringRouter {

    private final EnumMap<MonitoringType, Class<? extends Job>> registry;

    public MonitoringRouter(Set<MonitorJob> jobTypes) {
        this.registry = jobTypes.stream()
            .collect(Collectors.toMap(
                MonitorJob::monitorType, MonitorJob::getClass,
                (o1, o2) -> o1,
                () -> new EnumMap<>(MonitoringType.class)
            ));
    }

    public Class<? extends Job> getMonitor(MonitoringType type) {
        return registry.get(type);
    }
}
