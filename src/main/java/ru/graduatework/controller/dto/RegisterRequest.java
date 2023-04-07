package ru.graduatework.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.graduatework.entity.Role;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class RegisterRequest {

    private String firstname;

    private String lastname;

    @Schema(description = "Почта для регистрации")
    @NotNull
    private String email;

    @Schema(description = "Пароль")
    @NotNull
    private String password;

    private List<Role> roles;
}