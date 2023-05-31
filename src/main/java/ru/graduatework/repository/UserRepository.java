package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.graduatework.controller.dto.UpdateUserRequestDto;
import ru.graduatework.error.CommonException;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.UserRecord;

import java.time.OffsetDateTime;

import static ru.graduatework.error.Code.USER_DUPLICATE_EMAIL;
import static ru.graduatework.error.Code.USER_NOT_FOUND;
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

    public String getAvatarPath(PostgresOperatingContext ctx, Long id){
        return ctx.dsl().select(USER.AVATAR).from(USER).where(USER.ID.eq(id)).fetchOneInto(String.class);
    }

    public UserRecord getById(PostgresOperatingContext ctx, Long id) {
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

    public Integer countUsersByEmail(PostgresOperatingContext ctx, String email, Long userId) {
        var condition = DSL.noCondition();

        if (userId != null) {
            condition = condition.and(USER.ID.notEqual(userId));
        }

        return ctx.dsl().selectCount()
                .from(USER)
                .where(USER.EMAIL.eq(email).and(condition))
                .fetchOneInto(Integer.class);
    }

    public Boolean updateUserUpdated(PostgresOperatingContext ctx, OffsetDateTime date, Long userId){
        return ctx.dsl().update(USER)
                .set(USER.UPDATED, date)
                .where(USER.ID.eq(userId))
                .execute() == 1;
    }

    public Boolean updateUserAvatar(PostgresOperatingContext ctx, String newPath, Long userId){
        return ctx.dsl().update(USER)
                .set(USER.AVATAR, newPath)
                .where(USER.ID.eq(userId))
                .execute() == 1;
    }
    public Boolean updateUserInfo(PostgresOperatingContext ctx, Long userId, UpdateUserRequestDto updateDto) {
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

    public String uploadImage(MultipartFile image, Long userId) throws Exception {
        return fileSystemRepository.save(image.getBytes(), userId);
    }
}
