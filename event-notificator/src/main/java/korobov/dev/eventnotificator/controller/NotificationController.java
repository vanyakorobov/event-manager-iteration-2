package korobov.dev.eventnotificator.controller;

import korobov.dev.eventnotificator.dto.EventChangeNotificationDto;
import korobov.dev.eventnotificator.dto.MarkReadRequest;
import korobov.dev.eventnotificator.entity.Notification;
import korobov.dev.eventnotificator.mapper.NotificationMapper;
import korobov.dev.eventnotificator.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;
    private final NotificationMapper mapper;

    public NotificationController(NotificationService service, NotificationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * GET /notifications
     */
    @GetMapping
    public List<EventChangeNotificationDto> getUnread(
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        List<Notification> list = service.getUnreadNotifications(userId);
        return list.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * POST /notifications
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @RequestBody MarkReadRequest request
    ) {
        service.markAsRead(userId, request.getNotificationIds());
    }
}
