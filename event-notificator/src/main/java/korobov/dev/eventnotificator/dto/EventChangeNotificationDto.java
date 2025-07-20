package korobov.dev.eventnotificator.dto;

import java.util.Optional;

public class EventChangeNotificationDto {
    private Long eventId;
    private FieldChangeDto<String> name;
    private FieldChangeDto<Integer> maxPlaces;
    private FieldChangeDto<String> date;      // ISO строка
    private FieldChangeDto<String> cost;      // передавать как строку или decimal
    private FieldChangeDto<Integer> duration;
    private FieldChangeDto<Integer> locationId;
    // геттеры/сеттеры

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public FieldChangeDto<String> getName() {
        return name;
    }

    public void setName(FieldChangeDto<String> name) {
        this.name = name;
    }

    public FieldChangeDto<Integer> getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(FieldChangeDto<Integer> maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public FieldChangeDto<String> getDate() {
        return date;
    }

    public void setDate(FieldChangeDto<String> date) {
        this.date = date;
    }

    public FieldChangeDto<String> getCost() {
        return cost;
    }

    public void setCost(FieldChangeDto<String> cost) {
        this.cost = cost;
    }

    public FieldChangeDto<Integer> getDuration() {
        return duration;
    }

    public void setDuration(FieldChangeDto<Integer> duration) {
        this.duration = duration;
    }

    public FieldChangeDto<Integer> getLocationId() {
        return locationId;
    }

    public void setLocationId(FieldChangeDto<Integer> locationId) {
        this.locationId = locationId;
    }
}
