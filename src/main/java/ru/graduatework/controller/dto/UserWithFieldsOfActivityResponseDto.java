package ru.graduatework.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.common.Role;

import java.util.List;

@NoArgsConstructor
@Data
public class UserWithFieldsOfActivityResponseDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String city;
    private List<FieldsOfActivityResponseDto> fieldOfActivitys;

}
