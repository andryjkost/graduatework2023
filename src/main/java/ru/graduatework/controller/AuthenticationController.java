package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import ru.graduatework.controller.dto.AuthenticationRequestDto;
import ru.graduatework.controller.dto.AuthenticationResponseDto;
import ru.graduatework.services.AuthenticationService;
import ru.graduatework.controller.dto.RefreshJwtRequestDto;
import ru.graduatework.controller.dto.RegisterRequestDto;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(description = "Работа с авторизацией и аутентификацией", name = "AuthenticationController")
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(
            @RequestBody @NonNull RegisterRequestDto request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Аутентификация пользователя")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody @NonNull AuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Получение NewAccessToken по refreshToken")
    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponseDto> getNewAccessToken(@RequestBody RefreshJwtRequestDto request) {
        return ResponseEntity.ok(service.getAccessToken(request.getRefreshToken()));
    }

    @PostMapping("/refresh")
    @Deprecated
    public ResponseEntity<AuthenticationResponseDto> getNewRefreshToken(HttpServletRequest request, @RequestBody RefreshJwtRequestDto refreshJwtRequestDto) throws IOException {
        return ResponseEntity.ok(service.refresh(request, refreshJwtRequestDto.getRefreshToken()));
    }

}
