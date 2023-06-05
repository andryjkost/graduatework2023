package ru.graduatework.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class FieldsOfActivityResponseDto {
    private UUID id;
    private String name;
    private String description;
}
