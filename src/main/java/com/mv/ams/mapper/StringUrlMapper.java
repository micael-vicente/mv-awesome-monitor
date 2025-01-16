package com.mv.ams.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.net.MalformedURLException;
import java.net.URI;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StringUrlMapper {

    @Named("sanitizedURL")
    default String sanitizedURL(String source) {
        try {
            return URI.create(source).toURL().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
