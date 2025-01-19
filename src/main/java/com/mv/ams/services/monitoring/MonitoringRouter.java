package com.mv.ams.services.monitoring;

import org.quartz.Job;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Component that registers all available {@link MonitorJob}
 * by their discriminator.
 */
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

    /**
     * Gets the class matching the {@link MonitoringType} discriminator.
     * @param type the discriminator
     * @return the class that matches the discriminator
     */
    public Class<? extends Job> getMonitor(MonitoringType type) {
        return registry.get(type);
    }
}
