package com.mv.ams.mapper;

import com.mv.ams.persistence.model.MonitoringJobEntity;
import com.mv.ams.persistence.model.MonitoringJobResultEntity;
import com.mv.ams.services.MonitoringJob;
import com.mv.ams.services.MonitoringJobResult;
import com.mv.ams.services.UpdateMonitoringJob;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServicePersistenceMapper {

    MonitoringJob map(MonitoringJobEntity source);

    @Mapping(target = "results", ignore = true)
    MonitoringJobEntity map(MonitoringJob source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void update(UpdateMonitoringJob source, @MappingTarget MonitoringJobEntity target);

    @Mapping(target = "jobId", ignore = true)
    MonitoringJobResult map(MonitoringJobResultEntity source);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "job", ignore = true)
    MonitoringJobResultEntity map(MonitoringJobResult source);
}
