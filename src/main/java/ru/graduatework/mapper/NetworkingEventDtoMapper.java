package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NetworkingEventDtoMapper {
    NetworkingEventResponseDto map(NetworkingEventRecord record);

    List<NetworkingEventResponseDto> map(List<NetworkingEventRecord> recordList);
}
