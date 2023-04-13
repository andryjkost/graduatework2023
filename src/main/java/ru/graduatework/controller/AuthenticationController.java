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
import ru.graduatework.controller.dto.AuthenticationRequest;
import ru.graduatework.controller.dto.AuthenticationResponse;
import ru.graduatework.services.AuthenticationService;
import ru.graduatework.controller.dto.RefreshJwtRequest;
import ru.graduatework.controller.dto.RegisterRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(description = "Работа с авторизацией и аутентификацией", name = "AuthenticationController")
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @NonNull RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Аутентификация пользователя")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @NonNull AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Получение NewAccessToken по refreshToken")
    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        return ResponseEntity.ok(service.getAccessToken(request.getRefreshToken()));
    }

    @PostMapping("/refresh")
    @Deprecated
    public ResponseEntity<AuthenticationResponse> getNewRefreshToken(HttpServletRequest request, @RequestBody RefreshJwtRequest refreshJwtRequest) throws IOException {
        return ResponseEntity.ok(service.refresh(request, refreshJwtRequest.getRefreshToken()));
    }

}
