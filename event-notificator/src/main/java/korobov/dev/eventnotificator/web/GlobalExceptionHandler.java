package korobov.dev.eventnotificator.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadRequest(IllegalArgumentException ex) {
        return buildResponse("Некорректный запрос", ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorMessageResponse> handleForbidden(SecurityException ex) {
        return buildResponse("Недостаточно прав для выполнения операции", ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleServerError(Exception ex) {
        return buildResponse("Внутренняя ошибка сервера", ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessageResponse> buildResponse(
            String message, Exception ex, HttpStatus status) {

        ErrorMessageResponse body = new ErrorMessageResponse();
        body.setMessage(message);
        body.setDetailedMessage(ex.getMessage());
        body.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(body, status);
    }
}
