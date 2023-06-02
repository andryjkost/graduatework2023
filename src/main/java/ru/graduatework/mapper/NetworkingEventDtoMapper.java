package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.UpdateNetworkingEventRequestDto;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, NetworkingEventStatus.class, OffsetDateTime.class})
public interface NetworkingEventDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().getLeastSignificantBits())")
    @Mapping(target = "status", expression = "java(NetworkingEventStatus.TO_BE.name())")
    @Mapping(target = "startTime", expression = "java(OffsetDateTime.parse(requestDto.getStartTime()))")
    NetworkingEventRecord mapForCreate(NetworkingEventRequestDto requestDto);

    NetworkingEventRecord mapForUpdate(UpdateNetworkingEventRequestDto requestDto);

    NetworkingEventResponseDto map(NetworkingEventRecord record);

    //    @Mapping(target = "authorId", source = "consumer.email")
//    @Mapping(target = "authorFirstLastNames", source = "consumer.email")
    List<NetworkingEventResponseDto> map(List<NetworkingEventRecord> recordList);
}
