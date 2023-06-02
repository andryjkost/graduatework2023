package ru.graduatework.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.FieldsOfActivityRequestDto;
import ru.graduatework.controller.dto.FieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.controller.dto.UpdateFieldsOfActivityRequestDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.FieldOfActivityMapper;
import ru.graduatework.repository.FieldOfActivityRepository;

@Service
@RequiredArgsConstructor
public class FieldOfActivityService {

    private final PostgresOperatingDb db;
    private final FieldOfActivityRepository fieldOfActivityRepository;

    private final FieldOfActivityMapper fieldOfActivityMapper;

    public Mono<PaginatedResponseDto<FieldsOfActivityResponseDto>> getAll(int offset, int limit) {
        return db.execAsync(ctx -> fieldOfActivityRepository.getAll(ctx, offset, limit));
    }

    public Mono<Void> getById() {
        return Mono.empty();
    }

    public Mono<Boolean> create(FieldsOfActivityRequestDto requestDto) {
        var fieldOfActivityRecord = fieldOfActivityMapper.mapForCreate(requestDto);
        return db.execAsync(ctx -> fieldOfActivityRepository.create(ctx, fieldOfActivityRecord));
    }

    public Mono<Boolean> update(UpdateFieldsOfActivityRequestDto requestDto) {
        var updatefieldOfActivityRecord = fieldOfActivityMapper.mapForUpdate(requestDto);
        return db.execAsync(ctx -> {
            var fieldOfActivityRecord = fieldOfActivityRepository.getById(ctx, updatefieldOfActivityRecord.getId());
            if (updatefieldOfActivityRecord.getName() == null) {
                updatefieldOfActivityRecord.setName(fieldOfActivityRecord.getName());
            }
            if (updatefieldOfActivityRecord.getDescription() == null) {
                updatefieldOfActivityRecord.setDescription(fieldOfActivityRecord.getDescription());
            }
            return fieldOfActivityRepository.update(ctx, updatefieldOfActivityRecord, updatefieldOfActivityRecord.getId());
        });
    }
}
