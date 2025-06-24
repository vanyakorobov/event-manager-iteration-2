package korobov.dev.eventmanager.users.db;

import korobov.dev.eventmanager.users.domain.User;
import org.springframework.stereotype.Component;
import korobov.dev.eventmanager.users.domain.UserRole;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.age(),
                user.role().name(),
                user.passwordHash()
        );
    }

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getAge(),
                UserRole.valueOf(entity.getRole()),
                entity.getPasswordHash()
        );
    }

}
