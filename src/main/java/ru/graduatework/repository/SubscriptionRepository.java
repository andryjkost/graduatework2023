package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.common.State;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.SubscriptionRecord;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static ru.graduatework.jooq.tables.Subscription.SUBSCRIPTION;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SubscriptionRepository {

    public List<SubscriptionRecord> getByUserId(PostgresOperatingContext ctx, UUID userId){
        return ctx.dsl().selectFrom(SUBSCRIPTION).where(SUBSCRIPTION.USER_ID.eq(userId).and(SUBSCRIPTION.STATE.eq(State.ACTIVE.name()))).fetch();
    }
    public Boolean checkSubscription(PostgresOperatingContext ctx, UUID userId, UUID courseId){
        var subscription = ctx.dsl()
                .selectFrom(SUBSCRIPTION)
                .where(SUBSCRIPTION.USER_ID.eq(userId).and(SUBSCRIPTION.COURSE_ID.eq(courseId)).and(SUBSCRIPTION.STATE.eq(State.ACTIVE.name())))
                .fetchOne();
        return subscription == null;
    }

    public boolean subscribe(PostgresOperatingContext ctx,  UUID userId, UUID courseId){
        SubscriptionRecord subscriptionRecord = new SubscriptionRecord();

        subscriptionRecord.setId(UUID.randomUUID());
        subscriptionRecord.setUserId(userId);
        subscriptionRecord.setCourseId(courseId);
        subscriptionRecord.setSubscribedAt(OffsetDateTime.now());
        subscriptionRecord.setState(State.ACTIVE.name());

        return ctx.dsl().insertInto(SUBSCRIPTION)
                .set(subscriptionRecord)
                .execute() == 1;
    }
}
