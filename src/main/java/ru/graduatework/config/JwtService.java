package ru.graduatework.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.graduatework.controller.dto.UserWithRoleResponseDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret.access}")
    private String jwtAccessSecret;
    @Value("${jwt.secret.refresh}")
    private String jwtRefreshSecret;


    public String extractAccessUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractRefreshExpirationTime(String token){
        return extractRefreshClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsAccess(token);
        return claimsResolver.apply(claims);
    }

    public <T> T extractRefreshClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsRefresh(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(
            @NonNull UserWithRoleResponseDto userDetails
    ) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(240).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);

        return Jwts
                .builder()
                .setSubject(userDetails.getEmail())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(accessExpiration)
                .signWith(getAccessSignInKey(), SignatureAlgorithm.HS256)
                .claim("id", userDetails.getId())
                .claim("roles", userDetails.getRoles())
                .claim("firstName", userDetails.getFirstName())
                .claim("lastName", userDetails.getLastName())
                .compact();
    }

    public String generateRefreshToken(@NonNull UserWithRoleResponseDto user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(getRefreshSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractAccessUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, getAccessSignInKey());
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, getRefreshSignInKey());
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaimsAccess(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getAccessSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims extractAllClaimsRefresh(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getRefreshSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getAccessSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    private Key getRefreshSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, getAccessSignInKey());
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, getRefreshSignInKey());
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromJwt(String jwt){
        final Claims claims = getAccessClaims(jwt);
        return Long.parseLong(claims.getId());
    }
}
