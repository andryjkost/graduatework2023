package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.UpdateNetworkingEventRequestDto;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NetworkingEventDtoMapper {

    NetworkingEventRecord map(UpdateNetworkingEventRequestDto requestDto);

    NetworkingEventResponseDto map(NetworkingEventRecord record);

//    @Mapping(target = "authorId", source = "consumer.email")
//    @Mapping(target = "authorFirstLastNames", source = "consumer.email")
    List<NetworkingEventResponseDto> map(List<NetworkingEventRecord> recordList);
}
