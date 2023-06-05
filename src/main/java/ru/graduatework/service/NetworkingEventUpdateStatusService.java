package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.repository.NetworkingEventRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class NetworkingEventUpdateStatusService {

    @Value("${networking-event.duration}")
    private String durationOfEventDefault;

    private final PostgresOperatingDb db;
    private final NetworkingEventRepository networkingEventRepository;

    public Mono<Void> updateStatus(){
        return db.transactionalExecAsync(ctx->{
           var list = networkingEventRepository.getList(ctx);
           return null;
        });
    }
}
