package korobov.dev.eventnotificator.web;

import java.time.LocalDateTime;

public class ErrorMessageResponse {
    private String message;
    private String detailedMessage;
    private LocalDateTime dateTime;
    // геттеры/сеттеры

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
