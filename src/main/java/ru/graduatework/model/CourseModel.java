package ru.graduatework.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CourseModel {
    private UUID id;

    private String title;

    private String description;

    private String category;

    private List<String> features;

    private String pathToAvatar;

    private List<UUID> userSubscribedIds;

    private String linkPayment;

    private AuthorShortModel authorShortModel;

    private UUID userId;
}
