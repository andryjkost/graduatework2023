package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.CourseResponseDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(description = "Работа с курсами", name = "CourseController")
public class CourseController {

    @Operation(summary = "Получение курса по id")
    @GetMapping("/{id}")
    Mono<CourseResponseDto> getCourse(
            @Parameter(description = "Идентификатор курса") @PathVariable UUID id

    ){
        return Mono.just(new CourseResponseDto());
    }


    @Operation(summary = "Создание курса")
    @PostMapping("")
    Mono<CourseResponseDto> createCourse() {return Mono.just(new CourseResponseDto());}
}
