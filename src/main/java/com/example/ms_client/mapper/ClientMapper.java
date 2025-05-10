package com.example.ms_client.mapper;

import com.example.ms_client.dto.ClientDTO;
import com.example.ms_client.entity.Client;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Mapping(target = "createDateTime", source = "createDateTime", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "updateDateTime", source = "updateDateTime", qualifiedByName = "stringToLocalDateTime")
    Client toEntity(ClientDTO dto);

    @Mapping(target = "createDateTime", source = "createDateTime", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updateDateTime", source = "updateDateTime", qualifiedByName = "localDateTimeToString")
    ClientDTO toDto(Client entity);

    @Mapping(target = "createDateTime", ignore = true)
    @Mapping(target = "updateDateTime", expression = "java(java.time.LocalDateTime.now())")
    void updateClientFromDto(ClientDTO dto, @MappingTarget Client entity);

    @Named("localDateTimeToString")
    default String map(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime map(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, FORMATTER) : null;
    }
}
