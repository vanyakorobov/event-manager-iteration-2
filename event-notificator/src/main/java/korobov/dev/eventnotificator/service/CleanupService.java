package korobov.dev.eventnotificator.service;

import korobov.dev.eventnotificator.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CleanupService {

    private static final Logger log = LoggerFactory.getLogger(CleanupService.class);

    private final NotificationRepository repository;
    private final int daysToKeep;

    public CleanupService(NotificationRepository repository,
                          @Value("${app.notifications.cleanup.days:7}") int daysToKeep) {
        this.repository = repository;
        this.daysToKeep = daysToKeep;
    }

    @Scheduled(cron = "${app.notifications.cleanup.cron:0 */10 * * * *}")
    public void deleteOldNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysToKeep);
        int deleted = repository.deleteByCreatedAtBefore(threshold);
        log.info("CleanupService: удалено {} уведомлений старше {} дней (до {})",
                deleted, daysToKeep, threshold);
    }
}
