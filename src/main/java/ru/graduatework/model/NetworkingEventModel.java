package ru.graduatework.model;

import lombok.Builder;
import lombok.Data;
import ru.graduatework.common.NetworkingEventStatus;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class NetworkingEventModel {
    private UUID id;

    private String title;

    private String description;

    private String link;

    private OffsetDateTime startTime;

    private LocalTime durationOfEvent;

    private Boolean eventSubscriptionFlag;

    private NetworkingEventStatus status;

    private Long maximumNumberOfParticipants;

    private Long numberOfAvailableSeats;

    private String pathToAvatar;

    private AuthorShortModel authorShortModel;
}
