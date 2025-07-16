package korobov.dev.eventnotificator.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import korobov.dev.eventnotificator.dto.EventChangeKafkaMessage;
import korobov.dev.eventnotificator.dto.EventChangeNotificationDto;
import korobov.dev.eventnotificator.dto.FieldChangeDto;
import korobov.dev.eventnotificator.dto.FieldChange;
import korobov.dev.eventnotificator.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

/**
 * Маппер для преобразования сущности Notification
 * и её JSON-пэйлоада в DTO для REST API.
 */
@Component
public class NotificationMapper {

    private final ObjectMapper objectMapper;

    public NotificationMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Преобразует Notification в EventChangeNotificationDto.
     */
    public EventChangeNotificationDto toDto(Notification notification) {
        try {
            // Десериализация JSON-пэйлоада
            EventChangeKafkaMessage msg = objectMapper.readValue(
                    notification.getPayload(), EventChangeKafkaMessage.class);

            EventChangeNotificationDto dto = new EventChangeNotificationDto();
            dto.setEventId(msg.getEventId());

            // Маппинг полей изменений
            Optional.ofNullable(msg.getName())
                    .ifPresent(fc -> dto.setName(map(fc)));
            Optional.ofNullable(msg.getMaxPlaces())
                    .ifPresent(fc -> dto.setMaxPlaces(map(fc)));
            Optional.ofNullable(msg.getDate())
                    .ifPresent(fc -> dto.setDate(map(fc,
                            oldVal -> oldVal.toString(),
                            newVal -> newVal.toString())));
            Optional.ofNullable(msg.getCost())
                    .ifPresent(fc -> dto.setCost(map(fc,
                            oldVal -> oldVal.toPlainString(),
                            newVal -> newVal.toPlainString())));
            Optional.ofNullable(msg.getDuration())
                    .ifPresent(fc -> dto.setDuration(map(fc)));
            Optional.ofNullable(msg.getLocationId())
                    .ifPresent(fc -> dto.setLocationId(map(fc)));

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось десериализовать payload нотификации", e);
        }
    }

    /**
     * Утилитный метод для маппинга FieldChange<T> -> FieldChangeDto<T>
     */
    private <T> FieldChangeDto<T> map(FieldChange<T> fc) {
        return new FieldChangeDto<T>(
                fc.getOldField(),
                fc.getNewField()
        );
    }

    private <T, R> FieldChangeDto<R> map(
            FieldChange<T> fc,
            Function<T, R> oldMapper,
            Function<T, R> newMapper) {
        return new FieldChangeDto<R>(
                oldMapper.apply(fc.getOldField()),
                newMapper.apply(fc.getNewField())
        );
    }

}
