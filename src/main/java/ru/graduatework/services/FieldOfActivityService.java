package ru.graduatework.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.FieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.repository.FieldOfActivityRepository;

@Service
@RequiredArgsConstructor
public class FieldOfActivityService {

    private final PostgresOperatingDb db;
    private final FieldOfActivityRepository fieldOfActivityRepository;

    public Mono<PaginatedResponseDto<FieldsOfActivityResponseDto>>  getAll(int offset, int limit)
    {
        return db.execAsync(ctx->fieldOfActivityRepository.getAll(ctx, offset, limit));
    }

}
