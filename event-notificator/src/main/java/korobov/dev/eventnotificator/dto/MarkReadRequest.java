package korobov.dev.eventnotificator.dto;

import java.util.List;

public class MarkReadRequest {
    private List<Long> notificationIds;
    // геттер/сеттер

    public List<Long> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }
}
