package ru.graduatework.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NetworkingEventScheduler {

    @Value(value = "${networking-event-shedule.enable}")
    public Boolean enabled;

    @Scheduled(cron = "${networking-event-shedule.cron}")
    public void doJob(){
        if (enabled) {
            log.info("networking-event status update service started");

        }
        else {
            log.debug("networking-event status update service is disabled");
        }

    }
}
