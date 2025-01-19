package com.mv.ams.services.monitoring;

import com.mv.ams.services.MonitoringJobResult;

/**
 * Typification of result savers to allow
 * future extension.
 */
public interface MonitoringResultSaver {

    /**
     * Saves a monitoring result.
     * @param result the result to be saved
     */
    void saveResult(MonitoringJobResult result);
}
