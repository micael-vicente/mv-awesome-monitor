package com.mv.ams.services.monitoring;

import com.mv.ams.services.MonitoringType;
import org.quartz.Job;

public interface MonitorJob extends Job {
    MonitoringType monitorType();
}
