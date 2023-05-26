package ru.graduatework.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetworkingEventPaginatedFilter {
    private final int offset;
    private final int limit;
    private final NetworkingEventStatus status;
}
