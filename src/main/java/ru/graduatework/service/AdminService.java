package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.error.CommonException;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.repository.SubscriptionRepository;

import java.util.UUID;

import static ru.graduatework.error.Code.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final PostgresOperatingDb db;
    private final SubscriptionRepository subscriptionRepository;

    public Mono<Boolean> assignToCourseByUserId(UUID userId, UUID courseId) {
        return db.execAsync(ctx -> {
            var checkSubscription = subscriptionRepository.checkSubscription(ctx, userId, courseId);
            if (checkSubscription) {
                return subscriptionRepository.subscribe(ctx, userId, courseId);
            }
            log.info("User with id:{} has already been assigned a course with id:{}", userId, courseId);
            return null;
        });
    }

}
