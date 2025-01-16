package com.mv.ams.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StringHashingMapper {

    @Named("stringToCRC32")
    default String stringToCRC32(String value) {
        if(value == null) {
            return null;
        }

        CRC32 crc32Hash = new CRC32();
        crc32Hash.update(value.getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(crc32Hash.getValue());
    }

}

