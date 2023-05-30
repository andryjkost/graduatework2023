package ru.graduatework.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.common.Role;

@NoArgsConstructor
@Data
public class UserWithRolesResponseDto  {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Role role;
}
