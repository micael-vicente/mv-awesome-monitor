package com.mv.ams.mapper;

import com.mv.ams.services.MonitoringJob;
import com.mv.ams.web.api.CreateMonitoringJobDto;
import com.mv.ams.web.api.MonitoringJobDto;
import com.mv.ams.web.api.PageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {StringHashingMapper.class, StringUrlMapper.class})
public interface ServiceDtoMapper {

    @Mapping(target = "pagination.pageNumber", source = "number")
    @Mapping(target = "pagination.pageSize", source = "size")
    @Mapping(target = "pagination.totalElements", source = "totalElements")
    @Mapping(target = "pagination.numberOfElements", source = "numberOfElements")
    @Mapping(target = "pagination.totalPages", source = "totalPages")
    PageDto<MonitoringJobDto> map(Page<MonitoringJob> source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", source = "address", qualifiedByName = "sanitizedURL")
    @Mapping(target = "shortAddress", source = "address", qualifiedByName = "stringToCRC32")
    MonitoringJob map(CreateMonitoringJobDto source);

    MonitoringJobDto map(MonitoringJob source);
}
