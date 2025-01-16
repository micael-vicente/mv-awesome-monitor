package com.mv.ams.mapper;

import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.services.MonitoringJob;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServicePersistenceMapper {

    MonitoringJob map(MonitoringJobEntity source);

    MonitoringJobEntity map(MonitoringJob source);
}
