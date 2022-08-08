package oauth2.oauth2study.service;

import lombok.RequiredArgsConstructor;
import oauth2.oauth2study.entity.User;
import oauth2.oauth2study.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserResgistrationService {

    private final UserService userService;
    private final UserRepository userRepository;

    public void requestRegistration(
            final String name,
            final String email
    ) {
        final boolean exists = userService.existsByEmail(email);

        if (exists == false) {
            final User user = new User(name, email);
            userRepository.save(user);
        }
    }
}
