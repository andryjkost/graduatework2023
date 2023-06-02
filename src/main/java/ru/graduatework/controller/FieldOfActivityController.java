package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.FieldsOfActivityRequestDto;
import ru.graduatework.controller.dto.FieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.controller.dto.UpdateFieldsOfActivityRequestDto;
import ru.graduatework.services.FieldOfActivityService;

@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(description = "Работа со сферами деятельности", name = "FieldOfActivityController")
public class FieldOfActivityController {

    private final FieldOfActivityService fieldOfActivityService;

    @Operation(summary = "Получение полного списка сфер деятельностей")
    @GetMapping("/all")
    Mono<PaginatedResponseDto<FieldsOfActivityResponseDto>> getAll(
            @Parameter(description = "Отступ - число элементов, пропущенных от начала списка результатов")
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description = "Ограничение на число показанных результатов")
            @RequestParam(required = false, defaultValue = "0") int limit
    ) {
        return fieldOfActivityService.getAll(offset, limit);
    }

    @Operation(summary = "Получение сферы деятельности по id")
    @GetMapping("{id}")
    Mono<Void> getById() {
        return Mono.empty();
    }

    @Operation(summary = "  Создание сферы деятельности")
    @PostMapping("")
    Mono<Boolean> createFieldOfActivity(
            @RequestBody FieldsOfActivityRequestDto requestDto
    ) {

        return fieldOfActivityService.create(requestDto);
    }

    @Operation(summary = "Обновление сферы деятельности")
    @PutMapping("")
    Mono<Boolean> updateFieldOfActivityById(
            @RequestBody UpdateFieldsOfActivityRequestDto requestDto
    ) {
        return fieldOfActivityService.update(requestDto);
    }

    @Operation(summary = "")
    @DeleteMapping("")
    Mono<Void> deleteFieldOfActivityById() {
        return Mono.empty();
    }
}
