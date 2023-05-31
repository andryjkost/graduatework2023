package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.CourseResponseDto;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(description = "Работа с курсами", name = "CourseController")
public class CourseController {

    @Operation(summary = "Получение курсов")
    @GetMapping("")
    Mono<CourseResponseDto> getCourse(){
        return Mono.just(new CourseResponseDto());
    }
}
