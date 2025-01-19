package com.mv.ams.services.monitoring;

import org.quartz.Job;

/**
 * Extension of {@link Job} to allow typification so that new
 * Monitoring job types can be added seamlessly.
 */
public interface MonitorJob extends Job {
    /**
     * Discriminator of this MonitorJob.
     * @return discriminator
     */
    MonitoringType monitorType();
}
