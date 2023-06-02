package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.controller.dto.UpdateNetworkingEventRequestDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.NetworkingEventDtoMapper;
import ru.graduatework.repository.NetworkingEventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkingEventService {

    private final JwtService jwtService;
    private final AuthorService authorService;

    private final NetworkingEventRepository networkingEventRepository;
    private final PostgresOperatingDb db;

    private final NetworkingEventDtoMapper networkingEventDtoMapper;

    public Mono<NetworkingEventResponseDto> getById(Long id) {
        return db.execAsync(ctx -> networkingEventRepository.getById(ctx, id));
    }

    public Mono<Boolean> update(UpdateNetworkingEventRequestDto requestDto) {
        var networkingEventRecord = networkingEventDtoMapper.map(requestDto);
        return db.execAsync(ctx -> networkingEventRepository.update(ctx, networkingEventRecord, networkingEventRecord.getId()));
    }

    public Mono<PaginatedResponseDto<NetworkingEventResponseDto>> getPaginatedListOfEvents(NetworkingEventPaginatedFilter filter, String authToken) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        filter.setUserId(userId);

        return db.execAsync(ctx -> networkingEventRepository.getPaginatedListOfEvents(ctx, filter));
    }

    public Mono<NetworkingEventResponseDto> createNetworkingEvent(String authToken, NetworkingEventRequestDto requestDto) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        var author = authorService.getByUserId(userId);

        return db.execAsync(ctx -> networkingEventDtoMapper.map(networkingEventRepository.createNetworkingEvent(ctx, requestDto, author.getId())));
    }

}
