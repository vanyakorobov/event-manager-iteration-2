package korobov.dev.eventmanager.locations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationDto(
        Long id,
        @NotBlank(message = "Location name should be not blank")
        String name,
        @NotBlank(message = "Location address should be not blank")
        String address,
        @NotNull
        @Min(value = 5, message = "Minimum capacity of location is 5")
        Long capacity,
        String description
) {
}