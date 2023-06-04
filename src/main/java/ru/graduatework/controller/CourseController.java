package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.CourseRequestDto;
import ru.graduatework.controller.dto.CourseResponseDto;
import ru.graduatework.controller.dto.CourseResponseShortDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.service.CourseService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(description = "Работа с курсами", name = "CourseController")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Получение курса по id")
    @GetMapping("/{id}")
    Mono<CourseResponseDto> getCourseById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Идентификатор курса") @PathVariable UUID id

    ) {
        return courseService.getById(authToken, id);
    }

    @Operation(summary = "Получение курсов с пагинацией")
    @GetMapping("")
    Mono<PaginatedResponseDto<CourseResponseShortDto>> getCourse(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Отступ - число элементов, пропущенных от начала списка результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description = "Ограничение на число показанных результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        return courseService.getPaginated(offset, limit, authToken);
    }

    @Operation(summary = "Добавление/обновление Аватарки курса (JPEG в идиела, так хавает и PNG тоже)")
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Mono<Void> uploadAvatar(@RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
                            @Parameter(description = "Идентификатор курса") @PathVariable UUID id,
                            @RequestParam MultipartFile image) throws Exception {
        return courseService.uploadAvatar(authToken, image, id);
    }

    @Operation(summary = "Удаление Аватарки")
    @DeleteMapping(value = "/{id}/avatar")
    Mono<Void> deleteAvatar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Идентификатор курса") @PathVariable UUID id
    ) {
        return courseService.deletedAvatar(authToken, id);
    }

    @Operation(summary = "Создание курса")
    @PostMapping("")
    Mono<CourseResponseShortDto> createCourse(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @RequestBody CourseRequestDto requestDto
    ) {
        return courseService.create(authToken, requestDto);
    }
}
