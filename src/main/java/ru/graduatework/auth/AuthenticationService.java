package ru.graduatework.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.AuthenticationRequest;
import ru.graduatework.controller.dto.AuthenticationResponse;
import ru.graduatework.controller.dto.RegisterRequest;
import ru.graduatework.error.AuthException;
import ru.graduatework.repository.TokenRepository;
import ru.graduatework.repository.UserRepository;
import ru.graduatework.services.TokenService;
import ru.graduatework.services.UserService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegisterRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var newUser = userService.createUser(request);

        var jwtAccessToken = jwtService.generateAccessToken(newUser);
        var jwtRefreshToken = jwtService.generateRefreshToken(newUser);

//        saveUserRefreshToken(newUser.getId(), jwtAccessToken);
        tokenService.saveRefreshToken(newUser.getId(), jwtRefreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        //нужна ошибка если нет юзера
        var user = userService.getByEmail(request.getEmail());
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())){
            var jwtAccessToken = jwtService.generateAccessToken(user);
            var jwtRefreshToken = jwtService.generateAccessToken(user);

            tokenService.deleteByUserId(user.getId());
            tokenService.saveRefreshToken(user.getId(), jwtRefreshToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .build();
        }
        else {
            throw new AuthException("Неправильный пароль");
        }
    }

    public AuthenticationResponse getAccessToken(String jwtRefreshToken) {
        if (jwtService.validateRefreshToken(jwtRefreshToken)) {
            final Claims claims = jwtService.getRefreshClaims(jwtRefreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = tokenService.getByUserEmail(email);

            if (saveRefreshToken != null && saveRefreshToken.equals(jwtRefreshToken)) {
                final var user = userService.getByEmail(email);
//                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
                final String accessToken = jwtService.generateAccessToken(user);
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(null)
                        .build();
            }
        }
        return new AuthenticationResponse(null, null);
    }

    public AuthenticationResponse refresh(HttpServletRequest request, String jwtRefreshToken) throws IOException {

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
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }
}
