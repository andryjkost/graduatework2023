package ru.graduatework.services;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.AuthenticationRequestDto;
import ru.graduatework.controller.dto.AuthenticationResponseDto;
import ru.graduatework.controller.dto.RegisterRequestDto;
import ru.graduatework.error.AuthException;
import ru.graduatework.error.CommonException;

import java.io.IOException;

import static ru.graduatework.error.Code.USER_DUPLICATE_EMAIL;
import static ru.graduatework.error.Code.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public Mono<AuthenticationResponseDto> register(RegisterRequestDto request) {
        var checkUser = userService.getUserByEmail(request.getEmail()) != null;

        if (checkUser) {
            log.error("User duplicated by email");
            throw CommonException.builder().code(USER_DUPLICATE_EMAIL).userMessage("Пользователь c таким почтовым адресом уже существует").techMessage("User duplicated by email").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var newUser = userService.createUser(request);

        var jwtAccessToken = jwtService.generateAccessToken(newUser);
        var jwtRefreshToken = jwtService.generateRefreshToken(newUser);

        tokenService.saveRefreshToken(newUser.getId(), jwtRefreshToken);
        return Mono.just(AuthenticationResponseDto.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build());
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {

        //нужна ошибка если нет юзера
        var user = userService.getByEmail(request.getEmail());
        if (user == null) {
            throw CommonException.builder().code(USER_NOT_FOUND).userMessage("Пользователь c такой почтой не существует").techMessage("User with email: " + request.getEmail() + " not found").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            var jwtAccessToken = jwtService.generateAccessToken(user);
            var jwtRefreshToken = jwtService.generateRefreshToken(user);

            tokenService.deleteByUserId(user.getId());
            tokenService.saveRefreshToken(user.getId(), jwtRefreshToken);
            return AuthenticationResponseDto.builder()
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .build();
        } else {
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
