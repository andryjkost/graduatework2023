package ru.graduatework.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.graduatework.service.NetworkingEventUpdateStatusService;

@Component
@RequiredArgsConstructor
@Slf4j
public class NetworkingEventScheduler {

    private final NetworkingEventUpdateStatusService networkingEventUpdateStatusService;

    @Value(value = "${networking-event.shedule.enable}")
    public Boolean enabled;

    @Scheduled(cron = "${networking-event.shedule.cron}")
    public void doJob(){
        if (enabled) {
            log.info("networking-event status update service started");
            networkingEventUpdateStatusService.updateStatus()
                    .doOnError(t-> log.error("error on updating Status NetworkingEvent: {}", t.getMessage()))
                    .doOnSuccess(res-> log.info("NetworkingEvent update Status completed successfully"))
                    .subscribe();

        }
        else {
            log.debug("networking-event status update service is disabled");
        }
    }
}
