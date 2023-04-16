package ru.graduatework.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.common.ValidationUtils;

import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto {

    @Pattern(regexp = ValidationUtils.EMAIL_VALIDATION_PATTERN, message = "Адрес электронной почты должен быть корректным")
    private String email;
    private String password;
}
