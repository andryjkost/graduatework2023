package ru.graduatework.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FieldsOfActivityResponseDto {
    private Long id;
    private String name;
    private String description;
}
