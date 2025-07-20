package korobov.dev.eventnotificator.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Утилитный класс для сериализации объектов в JSON.
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {
        // приватный конструктор, чтобы предотвратить инстанцирование
    }

    /**
     * Сериализует объект в JSON-строку.
     *
     * @param obj объект для сериализации
     * @return JSON-представление объекта
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при сериализации объекта в JSON", e);
        }
    }
}
