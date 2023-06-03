package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.graduatework.common.FlagFile;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.*;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.NetworkingEventDtoMapper;
import ru.graduatework.repository.FileSystemRepository;
import ru.graduatework.repository.NetworkingEventRepository;

import java.io.IOException;
import java.util.UUID;

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

    public Mono<NetworkingEventResponseDto> getById(UUID id) {
        return db.execAsync(ctx -> {
            var model = networkingEventRepository.getById(ctx, id);
            var result = networkingEventDtoMapper.map(model);
            if (model.getPathToAvatar() != null) {
                try {
                    result.setImage(fileSystemRepository.findInFileSystem(model.getPathToAvatar()).getContentAsByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                result.setImage(new byte[0]);
            }
            return result;
        });
    }

    public Mono<Boolean> update(String authToken, UpdateNetworkingEventRequestDto requestDto) {
        var networkingEventRecord = networkingEventDtoMapper.mapForUpdate(requestDto);

        return db.execAsync(ctx -> {
            var avatarPath = networkingEventRepository.getAvatarPathById(ctx, networkingEventRecord.getId());
            if (requestDto.getImage() != null) {
                if (avatarPath != null) {
                    fileSystemRepository.delete(avatarPath);
                }
                try {
                    networkingEventRecord.setPathAvatar(fileSystemRepository.save(requestDto.getImage().getBytes(), networkingEventRecord.getId(), FlagFile.EVENT_AVATAR));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (avatarPath != null) {
                    fileSystemRepository.delete(avatarPath);
                }
            }
            return networkingEventRepository.update(ctx, networkingEventRecord, networkingEventRecord.getId());
        });
    }

    public Mono<PaginatedResponseDto<NetworkingEventResponseDto>> getPaginatedListOfEvents(NetworkingEventPaginatedFilter filter, String authToken) {
        var jwt = authToken.substring(7);
        var userId = UUID.fromString(jwtService.getUserIdFromJwt(jwt));
        filter.setUserId(userId);

        return db.execAsync(ctx -> {
            var tuple = networkingEventRepository.getPaginatedListOfEvents(ctx, filter);
            var result = tuple.getT2().stream().map(record -> {
                var networkingEventResponseDto = networkingEventDtoMapper.map(record);
                if (record.getPathToAvatar() != null) {
                    try {
                        networkingEventResponseDto.setImage(fileSystemRepository.findInFileSystem(record.getPathToAvatar()).getContentAsByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    networkingEventResponseDto.setImage(new byte[0]);
                }
                return networkingEventResponseDto;
            }).toList();

            return PaginatedResponseDto.<NetworkingEventResponseDto>builder()
                    .totalCount(tuple.getT1())
                    .count(result.size())
                    .result(result)
                    .build();
        });
    }

    public Mono<NetworkingEventResponseDto> createNetworkingEvent(String authToken, NetworkingEventRequestDto requestDto, MultipartFile image) {
        var userId = jwtService.getUserIdByToken(authToken.substring(7));
        var authorId = authorService.getByUserId(userId).getId();

        return db.execAsync(ctx -> {
            var newNetworkingEventRecord = networkingEventDtoMapper.mapForCreate(requestDto);
            networkingEventRepository.createNetworkingEvent(ctx, newNetworkingEventRecord, authorId);

            if (requestDto.getImage() != null) {
                String newPath = null;
                try {
                    newPath = fileSystemRepository.save(image.getBytes(), newNetworkingEventRecord.getId(), FlagFile.EVENT_AVATAR);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                networkingEventRepository.addAvatar(ctx, newPath, newNetworkingEventRecord.getId());
            }

            var model = networkingEventRepository.getById(ctx, newNetworkingEventRecord.getId());

            var result = networkingEventDtoMapper.map(model);
            if (!model.getPathToAvatar().isEmpty()) {
                try {
                    result.setImage(fileSystemRepository.findInFileSystem(model.getPathToAvatar()).getContentAsByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                result.setImage(new byte[0]);
            }
            return result;
        });
    }

}
