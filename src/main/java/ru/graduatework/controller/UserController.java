package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.services.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(description = "Работа с пользователями", name = "UserController")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение информации о Пользователе по глобальному идентификатору")
    @GetMapping("/{id}")
    Mono<UserWithFieldsOfActivityResponseDto> getById(
            @Parameter(description = "Глобальный идентификатор потребителя") @PathVariable Long id
    ){
        return userService.getById(id);
    }
}
