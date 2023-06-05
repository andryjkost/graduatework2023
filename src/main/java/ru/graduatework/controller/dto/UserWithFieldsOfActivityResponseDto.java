package ru.graduatework.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
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
