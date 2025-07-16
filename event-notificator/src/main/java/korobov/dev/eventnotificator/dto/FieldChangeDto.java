package korobov.dev.eventnotificator.dto;

/**
 * DTO для передачи информации об изменении поля в REST API.
 */
public class FieldChangeDto<T> {

    private T oldField;
    private T newField;

    // Конструктор по умолчанию (для Jackson)
    public FieldChangeDto() { }

    // Конструктор для удобного создания экземпляра
    public FieldChangeDto(T oldField, T newField) {
        this.oldField = oldField;
        this.newField = newField;
    }

    // Геттеры и сеттеры
    public T getOldField() {
        return oldField;
    }

    public void setOldField(T oldField) {
        this.oldField = oldField;
    }

    public T getNewField() {
        return newField;
    }

    public void setNewField(T newField) {
        this.newField = newField;
    }
}
