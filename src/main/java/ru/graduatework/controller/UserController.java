package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.services.UserService;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(description = "Работа с пользователями", name = "UserController")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение информации о Пользователе")
    @GetMapping("")
    Mono<UserWithFieldsOfActivityResponseDto> getFullUserByToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken
    ){
        return userService.getFullUserByToken(authToken);
    }
}
