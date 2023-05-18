package ru.graduatework.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.graduatework.common.ValidationUtils;
import ru.graduatework.common.Role;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class RegisterRequestDto {

    @Schema(description = "Имя", example = "Андрей")
    @Pattern(regexp = ValidationUtils.FIO_VALIDATION_PATTERN, message = "Неверный формат")
    private String firstname;

    @Schema(description = "Фамилия", example = "Фамилия")
    @Pattern(regexp = ValidationUtils.FIO_VALIDATION_PATTERN, message = "Неверный формат")
    private String lastname;

    @Schema(description = "Почта для регистрации", example = "123@mail.ru")
    @Pattern(regexp = ValidationUtils.EMAIL_VALIDATION_PATTERN, message = "Адрес электронной почты должен быть корректным")
    @NotNull
    private String email;

    @Schema(description = "Пароль")
    @NotNull
    private String password;

    private List<Role> roles;
}