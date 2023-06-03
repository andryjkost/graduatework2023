package ru.graduatework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Автор")
public class AuthorShortModel {

    @Schema(description = "ID")
    private UUID id;

    @Schema(description = "FI")
    private String firstLastName;

}
