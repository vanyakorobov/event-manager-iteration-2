package korobov.dev.eventmanager.users.api;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import korobov.dev.eventmanager.users.domain.AuthenticationService;
import korobov.dev.eventmanager.users.domain.User;
import korobov.dev.eventmanager.users.domain.UserRegistrationService;
import korobov.dev.eventmanager.users.domain.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;

    public UserController(
            UserService userService,
            UserRegistrationService userRegistrationService,
            AuthenticationService authenticationService
    ) {
        this.userService = userService;
        this.userRegistrationService = userRegistrationService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        log.info("Get request for sign-un: login={}", signUpRequest.login());
        var user = userRegistrationService.registerUser(signUpRequest);

        return ResponseEntity.status(201)
                .body(convertDomainUser(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        log.info("Get request for authenticate user: login={}", signInRequest.login());
        var token = authenticationService.authenticateUser(signInRequest);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserInfo(
            @PathVariable Long userId
    ) {
        log.info("Get request for get user info: userId={}", userId);
        var user = userService.getUserById(userId);
        return ResponseEntity.ok(convertDomainUser(user));
    }

    private UserDto convertDomainUser(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}
