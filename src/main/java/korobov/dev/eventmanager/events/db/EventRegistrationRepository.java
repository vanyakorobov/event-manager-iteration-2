package korobov.dev.eventmanager.events.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    @Query("""
        SELECT reg from EventRegistrationEntity reg
        where reg.event.id = :eventId
        and reg.userId = :userId
    """)
    Optional<EventRegistrationEntity> findRegistration(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId
    );

    @Query("""
        SELECT reg.event FROM EventRegistrationEntity reg
        WHERE reg.userId = :userId
    """)
    List<EventEntity> findRegisteredEvents(@Param("userId") Long userId);
}
