package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.repository.NetworkingEventRepository;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NetworkingEventUpdateStatusService {

    @Value("${networking-event.duration}")
    private String durationOfEventDefault;

    private final PostgresOperatingDb db;
    private final NetworkingEventRepository networkingEventRepository;

    public Mono<Void> updateStatus() {
        return db.transactionalExecAsync(ctx -> {
            var list = networkingEventRepository.getList(ctx);
            log.info("networking-event  update duration started");
            var listWithOutDuration = list.stream()
                    .filter(networkingEventRecord -> networkingEventRecord.getDurationOfEvent() == null).toList()
                    .stream().peek(record -> record.setDurationOfEvent(LocalTime.parse(durationOfEventDefault))).toList();
            if (listWithOutDuration != null && !listWithOutDuration.isEmpty()) {
                listWithOutDuration.forEach(record -> networkingEventRepository.update(ctx, record, record.getId()));
            }
            log.info("networking-event update duration completed successfully.");
            OffsetDateTime now = OffsetDateTime.now();
            list.forEach(networkingEventRecord -> {
                var endNetworkEvent = networkingEventRecord.getStartTime()
                        .plusHours(networkingEventRecord.getDurationOfEvent().getHour())
                        .plusMinutes(networkingEventRecord.getDurationOfEvent().getMinute())
                        .plusSeconds(networkingEventRecord.getDurationOfEvent().getSecond());

                if (NetworkingEventStatus.TO_BE.name().equals(networkingEventRecord.getStatus()) &&
                        (networkingEventRecord.getStartTime().isEqual(now) || networkingEventRecord.getStartTime().isBefore(now))) {

                    if (endNetworkEvent.isAfter(now) || endNetworkEvent.isEqual(now)) {
                        networkingEventRecord.setStatus(NetworkingEventStatus.PASSED.name());
                        log.info("Networking event with id: {} passed.", networkingEventRecord.getId());
                        networkingEventRepository.update(ctx, networkingEventRecord, networkingEventRecord.getId());
                    } else {
                        networkingEventRecord.setStatus(NetworkingEventStatus.IN_PROCESS.name());
                        log.info("Networking event with id: {} in process.", networkingEventRecord.getId());
                        networkingEventRepository.update(ctx, networkingEventRecord, networkingEventRecord.getId());

                    }
                }

                if (NetworkingEventStatus.IN_PROCESS.name().equals(networkingEventRecord.getStatus()) && (endNetworkEvent.isBefore(now) || endNetworkEvent.isAfter(now))) {
                    networkingEventRecord.setStatus(NetworkingEventStatus.PASSED.name());
                    log.info("Networking event with id: {} passed.", networkingEventRecord.getId());
                    networkingEventRepository.update(ctx, networkingEventRecord, networkingEventRecord.getId());
                }
            });
            return true;
        }).then();
    }


}
