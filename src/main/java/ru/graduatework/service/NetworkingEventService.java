package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.graduatework.common.FlagFile;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.controller.dto.UpdateNetworkingEventRequestDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.NetworkingEventDtoMapper;
import ru.graduatework.repository.FileSystemRepository;
import ru.graduatework.repository.NetworkingEventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkingEventService {

    private final JwtService jwtService;
    private final AuthorService authorService;

    private final FileSystemRepository fileSystemRepository;
    private final NetworkingEventRepository networkingEventRepository;
    private final PostgresOperatingDb db;

    private final NetworkingEventDtoMapper networkingEventDtoMapper;

    public Mono<NetworkingEventResponseDto> getById(Long id) {
        return db.execAsync(ctx -> networkingEventRepository.getById(ctx, id));
    }

    public Mono<FileSystemResource> getAvatar(String authToken, Long id) {
        return db.execAsync(ctx -> {
            var avatarPath = networkingEventRepository.getAvatarPathById(ctx, id);
            if (avatarPath != null) {
                FileSystemResource avatarFile = fileSystemRepository.findInFileSystem(avatarPath);
                return avatarFile;
            } else {
                return null;
            }
        });
    }

    public Mono<Boolean> update(UpdateNetworkingEventRequestDto requestDto) {
        var networkingEventRecord = networkingEventDtoMapper.mapForUpdate(requestDto);
        return db.execAsync(ctx -> networkingEventRepository.update(ctx, networkingEventRecord, networkingEventRecord.getId()));
    }

    public Mono<PaginatedResponseDto<NetworkingEventResponseDto>> getPaginatedListOfEvents(NetworkingEventPaginatedFilter filter, String authToken) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        filter.setUserId(userId);

        return db.execAsync(ctx -> networkingEventRepository.getPaginatedListOfEvents(ctx, filter));
    }

    public Mono<NetworkingEventResponseDto> createNetworkingEvent(String authToken, NetworkingEventRequestDto requestDto, MultipartFile image) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        var authorId = authorService.getByUserId(userId).getId();

        return db.execAsync(ctx -> {
            var newNetworkingEventRecord = networkingEventDtoMapper.mapForCreate(requestDto);
            networkingEventRepository.createNetworkingEvent(ctx, newNetworkingEventRecord, authorId);

            String newPath = null;
            try {
                newPath = fileSystemRepository.save(image.getBytes(), newNetworkingEventRecord.getId(), FlagFile.EVENT_AVATAR);
            } catch (Exception e) {
                e.printStackTrace();
            }

            networkingEventRepository.addAvatar(ctx, newPath, newNetworkingEventRecord.getId());


            return networkingEventRepository.getById(ctx, newNetworkingEventRecord.getId());
        });
    }

}
