package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.graduatework.common.FlagFile;
import ru.graduatework.common.Role;
import ru.graduatework.controller.dto.UpdateUserRequestDto;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.error.CommonException;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.UserRecord;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static ru.graduatework.error.Code.USER_NOT_FOUND;
import static ru.graduatework.jooq.Tables.ROLE;
import static ru.graduatework.jooq.Tables.USER_ROLE;
import static ru.graduatework.jooq.tables.User.USER;

import static java.util.Objects.isNull;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final FileSystemRepository fileSystemRepository;

    public UserRecord save(PostgresOperatingContext ctx, UserRecord record) {
        return ctx.dsl().insertInto(USER).set(record).returning().fetchOne();
    }

    public String getAvatarPath(PostgresOperatingContext ctx, UUID id) {
        return ctx.dsl().select(USER.AVATAR).from(USER).where(USER.ID.eq(id)).fetchOneInto(String.class);
    }

    public UserRecord getById(PostgresOperatingContext ctx, UUID id) {
        var result = ctx.dsl().selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOne();
        if (isNull(result)) {
            log.error("User with id {} not found", id);
            throw CommonException.builder()
                    .code(USER_NOT_FOUND)
                    .userMessage("Пользователь не найден")
                    .techMessage("User not found")
                    .httpStatus(HttpStatus.NOT_FOUND).build();
        }
        return result;
    }

    public UserRecord getByEmail(PostgresOperatingContext ctx, String email) {
        return ctx.dsl().selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOne();
    }

    public Integer countUsersByEmail(PostgresOperatingContext ctx, String email, UUID userId) {
        var condition = DSL.noCondition();

        if (userId != null) {
            condition = condition.and(USER.ID.notEqual(userId));
        }

        return ctx.dsl().selectCount()
                .from(USER)
                .where(USER.EMAIL.eq(email).and(condition))
                .fetchOneInto(Integer.class);
    }

    public Boolean updateUserUpdated(PostgresOperatingContext ctx, OffsetDateTime date, UUID userId) {
        return ctx.dsl().update(USER)
                .set(USER.UPDATED, date)
                .where(USER.ID.eq(userId))
                .execute() == 1;
    }

    public Boolean updateUserAvatar(PostgresOperatingContext ctx, String newPath, UUID userId) {
        return ctx.dsl().update(USER)
                .set(USER.AVATAR, newPath)
                .where(USER.ID.eq(userId))
                .execute() == 1;
    }

    public Boolean updateUserInfo(PostgresOperatingContext ctx, UUID userId, UpdateUserRequestDto updateDto) {
        return ctx.dsl().transactionResult(tctx -> {
            var userRecord =
                    tctx.dsl()
                            .selectFrom(USER)
                            .where(USER.ID.eq(updateDto.getId()))
                            .fetchOneInto(UserRecord.class);

            if (userRecord == null) {
                throw CommonException.builder().code(USER_NOT_FOUND).userMessage("Пользователь c id: " + userId + " не найден").techMessage("User with id: " + userId + " not found").httpStatus(HttpStatus.BAD_REQUEST).build();
            }

            if (updateDto.getEmail() != null) {
                userRecord.setEmail(updateDto.getEmail());
            }

            if (updateDto.getFirstname() != null) {
                userRecord.setFirstName(updateDto.getFirstname());
            }

            if (updateDto.getLastname() != null) {
                userRecord.setLastName(updateDto.getLastname());
            }

            if (updateDto.getBirthday() != null) {
                userRecord.setBirthday(updateDto.getBirthday());
            }

            if (updateDto.getDescription() != null) {
                userRecord.setDescription(updateDto.getDescription());
            }

            if (updateDto.getCity() != null) {
                userRecord.setCity(updateDto.getCity());
            }

            if (updateDto.getSocialNetwork() != null) {
                userRecord.setSocialNetwork(updateDto.getSocialNetwork());
            }

            var now = OffsetDateTime.now();

            userRecord.setUpdated(now);

            return 1 == tctx.dsl().executeUpdate(userRecord);
        });
    }

    public String uploadImage(MultipartFile image, UUID userId) throws Exception {
        return fileSystemRepository.save(image.getBytes(), userId, FlagFile.USER_AVATAR);
    }


    public Tuple2<Integer, List<UserWithFieldsOfActivityResponseDto>> getPaginated(PostgresOperatingContext ctx, int offset, int limit) {
        var selectQuery = ctx.dsl().select(USER.ID, USER.EMAIL, USER.FIRST_NAME, USER.LAST_NAME, USER.CITY)
                .from(
                        USER.leftJoin(USER_ROLE).on(USER.ID.eq(USER_ROLE.USER_ID))
                                .leftJoin(ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ID)))
                .where(ROLE.NAME.eq(Role.USER.name()));

        var totalCount = ctx.dsl()
                .selectCount()
                .from(selectQuery)
                .fetchOneInto(Integer.class);

        var result = selectQuery
                .offset(offset)
                .limit(limit > 0 ? limit : null)
                .fetch(record -> UserWithFieldsOfActivityResponseDto.builder()
                        .id((UUID) record.get(0))
                        .email((String) record.get(1))
                        .firstName((String) record.get(2))
                        .lastName((String) record.get(3))
                        .city((String) record.get(4))
                        .build());

        return Tuples.of(totalCount, result);
    }
}
