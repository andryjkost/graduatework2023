package ru.graduatework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.RegisterRequestDto;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.UserWithRoleResponseDto;
import ru.graduatework.controller.dto.UserWithRolesResponseDto;
import ru.graduatework.error.AuthException;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.jooq.tables.records.UserRecord;
import ru.graduatework.mapper.UserDtoMapper;
import ru.graduatework.repository.FieldOfActivityRepository;
import ru.graduatework.repository.RoleRepository;
import ru.graduatework.repository.UserRepository;
import ru.graduatework.repository.UserRoleRepository;
import ru.graduatework.common.Role;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepository;
    private final PostgresOperatingDb db;
    private final UserRoleRepository userRoleRepository;
    private final FieldOfActivityRepository fieldOfActivityRepository;

    private final UserDtoMapper mapper;

    private final JwtService jwtService;

    public Mono<UserWithFieldsOfActivityResponseDto> getFullUserByToken(String authToken) {
        var id = jwtService.getUserIdFromJwt(authToken);
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
            user.setRoles(roleRepository.getListRoleByUserId(ctx, user.getId()));
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
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                for (var roleName : request.getRoles()) {
                    userRoleRepository.setRoleForUser(ctx, roleName, newUser.getId());
                }
            } else {
                userRoleRepository.setRoleForUser(ctx, Role.USER, newUser.getId());
            }
            user.setRoles(roleRepository.getListRoleByUserId(ctx, newUser.getId()));
            return user;
        });
    }
}
