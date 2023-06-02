package ru.graduatework.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FieldsOfActivityRequestDto {
    private String name;
    private String description;
}
