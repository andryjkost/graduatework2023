package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;

import static ru.graduatework.common.ValidationUtils.EMAIL_VALIDATION_PATTERN;
import static ru.graduatework.common.ValidationUtils.FIO_VALIDATION_PATTERN;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Создаваемый пользователь")
public class UpdateUserRequestDto {

    @Schema(description = "id обновляемого пользователя(заберется из токена)")
    private UUID id;

    @Email(regexp = EMAIL_VALIDATION_PATTERN, message = "Адрес электронной почты должен быть корректным")
    @Schema(description = "Электронная почта")
    private String email;

    @Pattern(regexp = FIO_VALIDATION_PATTERN, message = "Неверный формат")
    @Schema(description = "Имя")
    private String firstname;

    @Pattern(regexp = FIO_VALIDATION_PATTERN, message = "Неверный формат")
    @Schema(description = "Фамилия")
    private String lastname;

    @Schema(description = "День рождения")
    private LocalDate birthday;

    @Schema(description = "Описание")
    private String description;

    @Schema(description = "Город")
    private String city;

    @Schema(description = "Соц сети")
    private String socialNetwork;
}
