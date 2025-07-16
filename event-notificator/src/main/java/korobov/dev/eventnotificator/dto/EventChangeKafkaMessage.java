package korobov.dev.eventnotificator.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для сообщений об изменении мероприятия, публикуемых в Kafka.
 */
public class EventChangeKafkaMessage {

    private Long eventId;
    private List<Long> users;
    private Long ownerId;
    private Long changedById;

    private FieldChange<String> name;
    private FieldChange<Integer> maxPlaces;
    private FieldChange<LocalDateTime> date;
    private FieldChange<BigDecimal> cost;
    private FieldChange<Integer> duration;
    private FieldChange<Integer> locationId;

    public EventChangeKafkaMessage() { }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public FieldChange<String> getName() {
        return name;
    }

    public void setName(FieldChange<String> name) {
        this.name = name;
    }

    public FieldChange<Integer> getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(FieldChange<Integer> maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public FieldChange<LocalDateTime> getDate() {
        return date;
    }

    public void setDate(FieldChange<LocalDateTime> date) {
        this.date = date;
    }

    public FieldChange<BigDecimal> getCost() {
        return cost;
    }

    public void setCost(FieldChange<BigDecimal> cost) {
        this.cost = cost;
    }

    public FieldChange<Integer> getDuration() {
        return duration;
    }

    public void setDuration(FieldChange<Integer> duration) {
        this.duration = duration;
    }

    public FieldChange<Integer> getLocationId() {
        return locationId;
    }

    public void setLocationId(FieldChange<Integer> locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "EventChangeKafkaMessage{" +
                "eventId=" + eventId +
                ", users=" + users +
                ", ownerId=" + ownerId +
                ", changedById=" + changedById +
                ", name=" + name +
                ", maxPlaces=" + maxPlaces +
                ", date=" + date +
                ", cost=" + cost +
                ", duration=" + duration +
                ", locationId=" + locationId +
                '}';
    }
}
