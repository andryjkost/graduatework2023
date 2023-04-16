package ru.graduatework.services;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.AuthenticationRequestDto;
import ru.graduatework.controller.dto.AuthenticationResponseDto;
import ru.graduatework.controller.dto.RegisterRequestDto;
import ru.graduatework.error.AuthException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponseDto register(RegisterRequestDto request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var newUser = userService.createUser(request);

        var jwtAccessToken = jwtService.generateAccessToken(newUser);
        var jwtRefreshToken = jwtService.generateRefreshToken(newUser);

//        saveUserRefreshToken(newUser.getId(), jwtAccessToken);
        tokenService.saveRefreshToken(newUser.getId(), jwtRefreshToken);
        return AuthenticationResponseDto.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {

        //нужна ошибка если нет юзера
        var user = userService.getByEmail(request.getEmail());
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            var jwtAccessToken = jwtService.generateAccessToken(user);
            var jwtRefreshToken = jwtService.generateAccessToken(user);

            tokenService.deleteByUserId(user.getId());
            tokenService.saveRefreshToken(user.getId(), jwtRefreshToken);
            return AuthenticationResponseDto.builder()
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .build();
        }
        else {
            throw new AuthException("Неправильный пароль");
        }
    }

    public AuthenticationResponseDto getAccessToken(String jwtRefreshToken) {
        if (jwtService.validateRefreshToken(jwtRefreshToken)) {
            final Claims claims = jwtService.getRefreshClaims(jwtRefreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = tokenService.getByUserEmail(email);

            if (saveRefreshToken != null && saveRefreshToken.equals(jwtRefreshToken)) {
                final var user = userService.getByEmail(email);
//                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
                final String accessToken = jwtService.generateAccessToken(user);
                return AuthenticationResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(null)
                        .build();
            }
        }
        return new AuthenticationResponseDto(null, null);
    }

    public AuthenticationResponseDto refresh(HttpServletRequest request, String jwtRefreshToken) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwtAccessToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException("Невалидный JWT токен");
        }

        jwtAccessToken = authHeader.substring(7);

        if (jwtService.validateAccessToken(jwtAccessToken)) {
            final Claims claims = jwtService.getAccessClaims(jwtAccessToken);
            userEmail = claims.getSubject();
            final String saveRefreshToken = tokenService.getByUserEmail(userEmail);

            if (saveRefreshToken != null && saveRefreshToken.equals(jwtRefreshToken)) {

                final var user = userService.getByEmail(userEmail);
//                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
                final String accessToken = jwtService.generateAccessToken(user);
                final String newRefreshToken = jwtService.generateRefreshToken(user);

                tokenService.deleteByUserId(user.getId());
                tokenService.saveRefreshToken(user.getId(), jwtRefreshToken);
                return AuthenticationResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }
}
