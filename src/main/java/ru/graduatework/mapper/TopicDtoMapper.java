package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.TopicRequestDto;
import ru.graduatework.controller.dto.TopicResponseDto;
import ru.graduatework.jooq.tables.records.TopicRecord;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface TopicDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "pathVideo", source = "linkToVideo")
    TopicRecord mapForCreate(TopicRequestDto topicRequestDto);

    List<TopicRecord> mapForCreate(List<TopicRequestDto> topicRequestDtoList);

    @Mapping(source = "pathVideo", target = "linkToVideo")
    TopicResponseDto map(TopicRecord topicRecord);

    List<TopicResponseDto> map(List<TopicRecord> topicRecords);

}
