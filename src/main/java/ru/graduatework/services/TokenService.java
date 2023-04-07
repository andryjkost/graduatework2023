package ru.graduatework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graduatework.entity.TokenType;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.jooq.tables.records.TokenRecord;
import ru.graduatework.repository.TokenRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final PostgresOperatingDb db;
    private final TokenRepository tokenRepository;

    public void deleteByUserId(Long userId) {
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

    public void saveRefreshToken(Long userId, String jwtRefreshToken) {

        TokenRecord newToken = new TokenRecord();

        newToken.setId(UUID.randomUUID().getLeastSignificantBits());
        newToken.setUserId(userId);
        newToken.setToken(jwtRefreshToken);
        newToken.setTokentype(TokenType.BEARER.toString());

        db.execute(ctx -> tokenRepository.save(ctx, newToken));
    }
}
