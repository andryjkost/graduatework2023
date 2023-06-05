package ru.graduatework.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CourseShortModel {
    private UUID id;

    private String title;

    private String description;

    private String category;

    private String pathToAvatar;

    private AuthorShortModel authorShortModel;
}
