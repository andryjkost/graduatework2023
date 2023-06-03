package ru.graduatework.common;

import lombok.*;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkingEventPaginatedFilter {
    private  int offset;
    private  int limit;
    private  NetworkingEventStatus status;
    private  Boolean eventSubscriptionFlag;
    private UUID userId;
}
