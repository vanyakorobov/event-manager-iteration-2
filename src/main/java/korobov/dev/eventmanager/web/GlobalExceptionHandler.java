package korobov.dev.eventmanager.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleCommonException(Exception exception) {
        log.error("Handle common exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Internal error",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(500)
                .body(messageResponse);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(Exception exception) {
        log.error("Handle entity not found exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Entity not found",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(404)
                .body(messageResponse);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Object> handleIllegalArgument(Exception exception) {
        log.error("Handle bad request exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Bad request",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(400)
                .body(messageResponse);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(Exception exception) {
        log.error("Handle bad credentials exception", exception);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Failed to authenticate",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(401)
                .body(messageResponse);
    }
}
