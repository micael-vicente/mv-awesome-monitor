package com.mv.ams.mapper;

import com.mv.ams.services.MonitoringJob;
import com.mv.ams.services.MonitoringJobResult;
import com.mv.ams.services.UpdateMonitoringJob;
import com.mv.ams.web.api.CreateMonitoringJobDto;
import com.mv.ams.web.api.MonitoringJobDto;
import com.mv.ams.web.api.MonitoringJobResultDto;
import com.mv.ams.web.api.PageDto;
import com.mv.ams.web.api.UpdateMonitoringJobDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceDtoMapper {

    @Mapping(target = "pagination.pageNumber", source = "number")
    @Mapping(target = "pagination.pageSize", source = "size")
    @Mapping(target = "pagination.totalElements", source = "totalElements")
    @Mapping(target = "pagination.numberOfElements", source = "numberOfElements")
    @Mapping(target = "pagination.totalPages", source = "totalPages")
    PageDto<MonitoringJobDto> mapJobs(Page<MonitoringJob> source);

    @Mapping(target = "pagination.pageNumber", source = "number")
    @Mapping(target = "pagination.pageSize", source = "size")
    @Mapping(target = "pagination.totalElements", source = "totalElements")
    @Mapping(target = "pagination.numberOfElements", source = "numberOfElements")
    @Mapping(target = "pagination.totalPages", source = "totalPages")
    PageDto<MonitoringJobResultDto> mapResults(Page<MonitoringJobResult> source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", source = "enabled", defaultValue = "true")
    @Mapping(target = "createdAt", ignore = true)
    MonitoringJob map(CreateMonitoringJobDto source);

    MonitoringJobDto map(MonitoringJob source);

    UpdateMonitoringJob map(UpdateMonitoringJobDto source);
}
