package main.java.korobov.dev.eventmanager.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    // геттеры/сеттеры, конструктор без args
}
