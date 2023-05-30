package ru.graduatework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.*;
import ru.graduatework.error.AuthException;
import ru.graduatework.error.CommonException;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.jooq.tables.records.UserRecord;
import ru.graduatework.mapper.UserDtoMapper;
import ru.graduatework.repository.*;
import ru.graduatework.common.Role;

import java.time.OffsetDateTime;
import java.util.UUID;

import static ru.graduatework.error.Code.USER_DUPLICATE_EMAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepository;
    private final PostgresOperatingDb db;
    private final UserRoleRepository userRoleRepository;
    private final FieldOfActivityRepository fieldOfActivityRepository;
    private final FileSystemRepository fileSystemRepository;

    private final UserDtoMapper mapper;

    private final JwtService jwtService;

    public Mono<UserWithFieldsOfActivityResponseDto> getFullUserByToken(String authHeader) {
        var jwt = authHeader.substring(7);
        var id = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        return db.execAsync(ctx -> {
            var user = mapper.mapById(userRepo.getById(ctx, id));
            user.setFieldOfActivitys(fieldOfActivityRepository.getListFieldOfActivityByUserId(ctx, id));
            return user;
        });
    }

    public UserRecord getUserByEmail(String email) {
        return db.execute(ctx -> userRepo.getByEmail(ctx, email));
    }

    public UserWithRolesResponseDto getByEmail(String email) {
        return db.execute(ctx -> {
            var user = mapper.map(userRepo.getByEmail(ctx, email));
            if (user == null) {
                throw new AuthException("Нет юзера с такой почтой");
            }
            user.setRole(roleRepository.getRoleByUserId(ctx, user.getId()));
            return user;
        });
    }

    public UserWithRoleResponseDto getByEmailWithRole(String email) {
        return db.execute(ctx -> {
            var user = mapper.mapW(userRepo.getByEmail(ctx, email));
            if (user == null) {
                throw new AuthException("Нет юзера с такой почтой");
            }
            user.setRole(roleRepository.getRoleByUserId(ctx, user.getId()));
            return user;
        });
    }

    public UserWithRolesResponseDto createUser(RegisterRequestDto request) {
        UserRecord newUser = new UserRecord();

        newUser.setId(UUID.randomUUID().getLeastSignificantBits());
        newUser.setFirstName(request.getFirstname());
        newUser.setLastName(request.getLastname());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());

        return db.execute(ctx -> {
            var user = mapper.map(userRepo.save(ctx, newUser));
            if (request.getRole() != null) {
                userRoleRepository.setRoleForUser(ctx, request.getRole(), newUser.getId());
            } else {
                userRoleRepository.setRoleForUser(ctx, Role.USER, newUser.getId());
            }
            user.setRole(roleRepository.getRoleByUserId(ctx, newUser.getId()));
            return user;
        });
    }

    public Mono<Void> updateUserInfo(String authToken, UpdateUserRequestDto updateDto) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        updateDto.setId(userId);
        checkDuplicate(updateDto.getEmail(), userId);

        return db.execAsync(ctx ->
                        userRepo.updateUserInfo(ctx, userId, updateDto))
                .then();
    }

    private void checkDuplicate(String email, Long userId) {

        var checkDuplicate = db.execute(ctx -> userRepo.countUsersByEmail(ctx, email, userId) != 0);
        if (checkDuplicate) {
            log.error("User duplicated by email");
            throw CommonException.builder().code(USER_DUPLICATE_EMAIL).userMessage("Пользователь c таким почтовым адресом уже существует").techMessage("User duplicated by email").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
    }

    public Mono<Void> uploadAvatar(String authToken, MultipartFile image) throws Exception {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        var user = db.execute(ctx -> userRepo.getById(ctx, userId));
        if (image == null) {
            if (user.getAvatar() != null) {
                fileSystemRepository.delete(user.getAvatar());
            }
            return Mono.empty();
        }
        db.execute(ctx -> userRepo.updateUserUpdated(ctx, OffsetDateTime.now(), userId));
        var newPath = userRepo.uploadImage(image, userId);
        db.execute(ctx -> userRepo.updateUserAvatar(ctx, newPath, userId));
        return Mono.empty();
    }

    public Mono<Void> deletedAvatar(String authToken) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        var avatarPath = db.execute(ctx -> userRepo.getAvatarPath(ctx, userId));
        fileSystemRepository.delete(avatarPath);
        return Mono.empty();
    }

    public Mono<FileSystemResource> getAvatar(String authToken) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));
        var avatarPath = db.execute(ctx -> userRepo.getAvatarPath(ctx, userId));
        FileSystemResource avatarFile = fileSystemRepository.findInFileSystem(avatarPath);
        return Mono.just(avatarFile);
    }
}
