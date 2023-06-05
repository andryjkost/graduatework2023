package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.graduatework.controller.dto.CourseRequestDto;
import ru.graduatework.controller.dto.CourseResponseDto;
import ru.graduatework.controller.dto.CourseResponseShortDto;
import ru.graduatework.jooq.tables.records.CourseRecord;
import ru.graduatework.model.CourseModel;
import ru.graduatework.model.CourseShortModel;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface CourseDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    CourseRecord mapForCreate(CourseRequestDto dto);

    CourseResponseShortDto map(CourseShortModel model);

    CourseResponseShortDto map(CourseRecord model);

    @Mapping(target = "flagPayment", source = "model", qualifiedByName = "сheckSubscribeOnCourse")
    CourseResponseDto map(CourseModel model);

    @Named("сheckSubscribeOnCourse")
    static Boolean сheckSubscribeOnCourse(CourseModel model) {
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
