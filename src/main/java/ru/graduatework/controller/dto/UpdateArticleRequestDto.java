package ru.graduatework.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateArticleRequestDto extends ArticleRequestDto {
    private Long id;
}
