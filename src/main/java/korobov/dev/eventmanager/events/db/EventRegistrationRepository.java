package korobov.dev.eventmanager.events.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    @Query("""
        SELECT reg
        FROM EventRegistrationEntity reg
        WHERE reg.event.id = :eventId
          AND reg.userId    = :userId
    """)
    Optional<EventRegistrationEntity> findRegistration(
            @Param("eventId") Long eventId,
            @Param("userId")  Long userId
    );

    Optional<EventRegistrationEntity> findByEvent_IdAndUserId(Long eventId, Long userId);

    @Query("""
        SELECT reg.event
        FROM EventRegistrationEntity reg
        WHERE reg.userId = :userId
    """)
    List<EventEntity> findRegisteredEvents(@Param("userId") Long userId);
}
