package ru.graduatework.model;

import lombok.Builder;
import lombok.Data;
import ru.graduatework.common.NetworkingEventStatus;

import java.time.OffsetDateTime;

@Data
@Builder
public class NetworkingEventModel {
    private Long id;

    private String title;

    private String description;

    private String link;

    private OffsetDateTime startTime;

    private NetworkingEventStatus status;

    private String maximumNumberOfParticipants;

    private String numberOfAvailableSeats;
    private Long authorId;
    private String authorFirstLastNames;
}
