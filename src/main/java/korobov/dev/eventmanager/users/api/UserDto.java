package korobov.dev.eventmanager.users.api;

import korobov.dev.eventmanager.users.domain.UserRole;

public record UserDto(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
