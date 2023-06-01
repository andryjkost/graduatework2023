package ru.graduatework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Автор")
public class AuthorShortModel {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "FI")
    private String firstLastName;

}
