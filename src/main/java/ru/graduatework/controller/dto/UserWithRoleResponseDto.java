package ru.graduatework.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.entity.Role;

import java.util.List;

@NoArgsConstructor
@Data
public class UserWithRoleResponseDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private List<Role> roles;
}
