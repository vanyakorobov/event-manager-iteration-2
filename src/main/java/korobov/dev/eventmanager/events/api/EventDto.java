package korobov.dev.eventmanager.events.api;

import jakarta.validation.constraints.*;
import korobov.dev.eventmanager.events.domain.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDto(
        @NotNull(message = "ID is mandatory")
        Long id,

        @NotBlank(message = "Name is mandatory")
        String name,

        @NotBlank(message = "Owner ID is mandatory")
        Long ownerId,

        @Positive(message = "Maximum places must be greater than zero")
        int maxPlaces,

        @PositiveOrZero(message = "Occupied places must be non-negative")
        int occupiedPlaces,

        @NotNull(message = "Date is mandatory")
        LocalDateTime date,

        @PositiveOrZero(message = "Cost must be non-negative")
        int cost,

        @Min(value = 30, message = "Duration must be at least 30 minutes")
        int duration,

        @NotNull(message = "Location ID is mandatory")
        Long locationId,

        @NotNull(message = "Status is mandatory")
        EventStatus status
) {

}

