package br.inatel.carmanager.listener;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.inatel.carmanager.model.rest.Notification;
import br.inatel.carmanager.service.BrandManagerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!test")
public class NotificationListener implements ApplicationListener<ApplicationReadyEvent>
{
    private BrandManagerService brandManagerService;

    @Autowired
    public NotificationListener(BrandManagerService brandManagerService)
    {
        this.brandManagerService = brandManagerService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event)
    {
        Notification[] notificationList = brandManagerService.registerOnBrandManager();

        String response = Stream.of(notificationList)
                                .map(notification -> String.format("%s:%s", notification.getHost(),
                                                                   notification.getPort()))
                                .reduce((n1, n2) -> String.format("%s,%s", n1, n2))
                                .orElse("");

        log.info("Response: {}", String.format("[%s]", response));
    }
}
