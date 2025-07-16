package korobov.dev.eventnotificator.repository;

import korobov.dev.eventnotificator.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    // Удалить все нотификации старше заданного времени
    int deleteByCreatedAtBefore(LocalDateTime dateTime);
}
