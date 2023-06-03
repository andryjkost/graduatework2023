package ru.graduatework.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
public class UserWithFieldsOfActivityResponseDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String city;
    private List<FieldsOfActivityResponseDto> fieldOfActivitys;

}
