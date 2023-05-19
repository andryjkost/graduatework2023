package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.UpdateUserRequestDto;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.services.UserService;

import javax.validation.Valid;


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
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken
    ) {
        return userService.getFullUserByToken(authToken);
    }

    @Operation(summary = "Редактирование информации о Пользователе")
    @PutMapping("")
    Mono<Void> updateUserInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Обновленная информация о пользователе, изменяются только заполненные поля")
            @Valid
            @RequestBody UpdateUserRequestDto updateDto
    ) {
        return userService.updateUserInfo(authToken, updateDto);
    }

    //Работа с Avatar
    @Operation(summary = "Добавление/обновление Аватарки пользователя (JPEG в идиела, так хавает и PNG тоже)")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Mono<Void> uploadAvatar(@RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
                            @RequestParam MultipartFile image) throws Exception {
        return userService.uploadAvatar(authToken, image);
    }

    @Operation(summary = "Удаление Аватарки")
    @DeleteMapping(value = "/avatar")
    Mono<Void> deleteAvatar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken
    ){
        return userService.deletedAvatar(authToken);
    }

    @Operation(summary = "Получение Аватарки")
    @GetMapping(value = "/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    Mono<FileSystemResource> getAvatar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken
    ){
        return userService.getAvatar(authToken);
    }
}
