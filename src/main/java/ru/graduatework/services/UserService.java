package ru.graduatework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graduatework.controller.dto.RegisterRequest;
import ru.graduatework.controller.dto.UserWithRoleResponseDto;
import ru.graduatework.common.Role;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.jooq.tables.records.UserRecord;
import ru.graduatework.mapper.UserDtoMapper;
import ru.graduatework.repository.RoleRepository;
import ru.graduatework.repository.UserRepository;
import ru.graduatework.repository.UserRoleRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepository;
    private final PostgresOperatingDb db;
    private final UserDtoMapper mapper;
    private final UserRoleRepository userRoleRepository;

    public UserWithRoleResponseDto getByEmail(String email) {
        return db.execute(ctx -> {
            var user = mapper.map(userRepo.getByEmail(ctx, email));
            user.setRoles(roleRepository.getListRoleByUserId(ctx, user.getId()));
            return user;
        });
    }

    public UserWithRoleResponseDto createUser(RegisterRequest request) {
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
                userRoleRepository.setRoleForUser(ctx, Role.ROLE_USER, newUser.getId());
            }
            user.setRoles(roleRepository.getListRoleByUserId(ctx, newUser.getId()));
            return user;
        });
    }
}
