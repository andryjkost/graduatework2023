package ru.graduatework.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateFieldsOfActivityRequestDto extends FieldsOfActivityRequestDto {
    private UUID id;
}
