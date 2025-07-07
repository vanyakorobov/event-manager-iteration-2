package korobov.dev.eventmanager.events.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import korobov.dev.eventmanager.events.domain.EventRegistrationService;
import korobov.dev.eventmanager.users.domain.AuthenticationService;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class EventRegistrationController {

    private final static Logger log = LoggerFactory.getLogger(EventRegistrationController.class);

    private final EventRegistrationService eventRegistrationService;
    private final EventDtoMapper eventDtoMapper;
    private final AuthenticationService authenticationService;

    public EventRegistrationController(EventRegistrationService eventRegistrationService,
                                       EventDtoMapper eventDtoMapper,
                                       AuthenticationService authenticationService) {
        this.eventRegistrationService = eventRegistrationService;
        this.eventDtoMapper = eventDtoMapper;
        this.authenticationService = authenticationService;
    }

    @RequestMapping("/{eventId}")
    public ResponseEntity<Void> registerOnEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for register on event: eventId={}", eventId);

        eventRegistrationService.registerUserOnEvent(
                authenticationService.getCurrentAuthenticatedUser(),
                eventId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegisterOnEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for cancel register on event: eventId={}", eventId);

        eventRegistrationService.cancelUserRegistration(
                authenticationService.getCurrentAuthenticatedUser(),
                eventId
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getUserRegistrations() {
        log.info("Get request for get all user registrations");

        var foundEvents = eventRegistrationService.getUserRegisteredEvents(
                authenticationService.getCurrentAuthenticatedUser().id()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foundEvents.stream()
                        .map(eventDtoMapper::toDto)
                        .toList()
                );
    }
}
