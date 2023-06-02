package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class NetworkingEventUpdateStatusService {

    public Mono<Void> updateStatus(){
        return Mono.empty();
    }
}
