package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graduatework.config.JwtService;
import ru.graduatework.common.TokenType;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.jooq.tables.records.TokenRecord;
import ru.graduatework.repository.TokenRepository;

import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;

    private final PostgresOperatingDb db;
    private final TokenRepository tokenRepository;

    public void deleteByUserId(UUID userId) {
        db.execute(ctx -> {
            ctx.dsl().transaction(tctx -> tokenRepository.deleteByUserId(ctx, userId));
            return true;
        });
    }

    public String getByUserEmail(String email) {
        return db.execute(ctx -> tokenRepository.getByUserEmail(ctx, email).getToken());
    }

    public TokenRecord getByToken(String token){
        return db.execute(ctx-> tokenRepository.getByToken(ctx, token));
    }

    public void saveRefreshToken(UUID userId, String jwtRefreshToken) {

        TokenRecord newToken = new TokenRecord();

        newToken.setId(UUID.randomUUID());
        newToken.setUserId(userId);
        newToken.setToken(jwtRefreshToken);
        newToken.setTokentype(TokenType.BEARER.toString());
        newToken.setExpired(false);
        newToken.setExpirationTime(jwtService.extractRefreshExpirationTime(jwtRefreshToken).toInstant().atOffset(ZoneOffset.of("+03:00")));
        db.execute(ctx -> tokenRepository.save(ctx, newToken));
    }
}
