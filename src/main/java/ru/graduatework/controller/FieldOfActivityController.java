package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.FieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.services.FieldOfActivityService;

@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
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
    )
    {
        return fieldOfActivityService.getAll(offset, limit);
    }
}
