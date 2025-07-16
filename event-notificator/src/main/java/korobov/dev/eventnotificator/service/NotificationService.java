package korobov.dev.eventnotificator.service;

import korobov.dev.eventnotificator.entity.Notification;
import korobov.dev.eventnotificator.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    /**
     * Возвращает все непрочитанные нотификации пользователя.
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return repository.findByUserIdAndIsReadFalse(userId);
    }

    /**
     * Помечает переданные ID нотификаций как прочитанные, только если они
     * принадлежат этому пользователю. Игнорирует остальные.
     */
    @Transactional
    public void markAsRead(Long userId, List<Long> notificationIds) {
        // получаем все нотификации по списку ID и userId
        List<Notification> notifications =
                repository.findAllById(notificationIds).stream()
                        .filter(n -> n.getUserId().equals(userId))
                        .toList();

        notifications.forEach(n -> n.setIsRead(true));
        repository.saveAll(notifications);
    }
}
