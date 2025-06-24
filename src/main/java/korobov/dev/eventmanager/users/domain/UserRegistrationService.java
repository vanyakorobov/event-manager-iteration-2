package korobov.dev.eventmanager.users.domain;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import korobov.dev.eventmanager.users.api.SignUpRequest;

@Service
public class UserRegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(SignUpRequest signUpRequest) {
        if (userService.isUserExistsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("User with such login already exists");
        }
        var hashedPass = passwordEncoder.encode(signUpRequest.password());
        var user = new User(
                null,
                signUpRequest.login(),
                signUpRequest.age(),
                UserRole.USER,
                hashedPass
        );
        return userService.saveUser(user);
    }

}
