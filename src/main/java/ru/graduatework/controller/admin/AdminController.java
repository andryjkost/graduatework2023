package ru.graduatework.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.service.AdminService;
import ru.graduatework.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(description = "Работа с пользователями(админка", name = "AdminController")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    @Operation(summary = "Получение списка пользователей")
    @GetMapping("/users")
    Mono<PaginatedResponseDto<UserWithFieldsOfActivityResponseDto>> getFullUserByToken(
            @Parameter(description = "Отступ - число элементов, пропущенных от начала списка результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description = "Ограничение на число показанных результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        return userService.getPaginated(offset, limit);
    }

    @Operation(summary = "Назначение на курс пользователя")
    @PostMapping("/assignToCourse")
    Mono<Boolean> assignToCourseByUserId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Идентификатор пользователя") @RequestParam UUID userId,
            @Parameter(description = "Идентификатор курса") @RequestParam UUID courseId
    ) {
        return adminService.assignToCourseByUserId(authToken, userId, courseId);
    }

    @Operation(summary = "Назначение на курс пользователя")
    @PostMapping("/linkingArticleWithCourse")
    Mono<Boolean> linkingArticleWithCourse(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Идентификатор статьи") @RequestParam UUID articleId,
            @Parameter(description = "Идентификатор курса") @RequestParam UUID courseId
    ) {
        return adminService.linkingArticleWithCourse(authToken, articleId, courseId);
    }

}
