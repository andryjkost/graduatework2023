package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.UpdateNetworkingEventRequestDto;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;
import ru.graduatework.model.NetworkingEventModel;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, NetworkingEventStatus.class, OffsetDateTime.class})
public interface NetworkingEventDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", expression = "java(NetworkingEventStatus.TO_BE.name())")
    @Mapping(target = "startTime", expression = "java(OffsetDateTime.parse(requestDto.getStartTime()))")
    @Mapping(target = "numberOfAvailableSeats", source = "maximumNumberOfParticipants")
    NetworkingEventRecord mapForCreate(NetworkingEventRequestDto requestDto);

    @Mapping(target = "startTime", expression = "java(OffsetDateTime.parse(requestDto.getStartTime()))")
    NetworkingEventRecord mapForUpdate(UpdateNetworkingEventRequestDto requestDto);

    NetworkingEventResponseDto map(NetworkingEventRecord record);

    NetworkingEventResponseDto map(NetworkingEventModel record);

    //    @Mapping(target = "authorId", source = "consumer.email")
//    @Mapping(target = "authorFirstLastNames", source = "consumer.email")
    List<NetworkingEventResponseDto> map(List<NetworkingEventRecord> recordList);
}
