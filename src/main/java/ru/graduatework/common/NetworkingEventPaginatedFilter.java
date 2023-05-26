package ru.graduatework.common;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkingEventPaginatedFilter {
    private  int offset;
    private  int limit;
    private  NetworkingEventStatus status;
    private  Boolean eventSubscriptionFlag;
    private  Long userId;
}
