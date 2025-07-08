package korobov.dev.eventmanager.events.db;

import jakarta.persistence.*;

@Entity
@Table(name = "registrations")
public class EventRegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    public EventRegistrationEntity() {
    }

    public EventRegistrationEntity(Long id, Long userId, EventEntity event) {
        this.id = id;
        this.userId = userId;
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }
}
