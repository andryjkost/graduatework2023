package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.UpdateNetworkingEventRequestDto;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;
import ru.graduatework.model.NetworkingEventModel;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, NetworkingEventStatus.class, OffsetDateTime.class, LocalTime.class})
public interface NetworkingEventDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", expression = "java(NetworkingEventStatus.TO_BE.name())")
    @Mapping(target = "startTime", expression = "java(OffsetDateTime.parse(requestDto.getStartTime()))")
    @Mapping(target = "numberOfAvailableSeats", source = "maximumNumberOfParticipants")
    @Mapping(target = "durationOfEvent", expression = "java(LocalTime.parse(requestDto.getDurationOfEvent()))")
    NetworkingEventRecord mapForCreate(NetworkingEventRequestDto requestDto);

    @Mapping(target = "startTime", expression = "java(OffsetDateTime.parse(requestDto.getStartTime()))")
    NetworkingEventRecord mapForUpdate(UpdateNetworkingEventRequestDto requestDto);

    NetworkingEventResponseDto map(NetworkingEventRecord record);

    @Mapping(target = "eventSubscriptionFlag", source = "record", qualifiedByName = "сheckSubscribeOnEvent")
    NetworkingEventResponseDto map(NetworkingEventModel record);

    //    @Mapping(target = "authorId", source = "consumer.email")
//    @Mapping(target = "authorFirstLastNames", source = "consumer.email")
    List<NetworkingEventResponseDto> map(List<NetworkingEventRecord> recordList);

    @Named("сheckSubscribeOnEvent")
    static Boolean сheckSubscribeOnEvent(NetworkingEventModel model) {
        var userIdList = model.getUserSubscribedIds();
        if(userIdList != null){
            for (UUID id:userIdList) {
                if(id.equals(model.getUserId())){
                    return  true;
                }
            }
        }
        return false;
    }
}
