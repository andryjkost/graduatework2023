package ru.graduatework.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.common.TokenType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto {

    private final String type = TokenType.BEARER.toString();
    private String accessToken;
    private String refreshToken;

}