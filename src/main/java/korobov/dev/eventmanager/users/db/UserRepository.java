package korobov.dev.eventmanager.users.db;

import org.springframework.data.jpa.repository.JpaRepository;
import korobov.dev.eventmanager.users.db.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLogin(String login);
}
