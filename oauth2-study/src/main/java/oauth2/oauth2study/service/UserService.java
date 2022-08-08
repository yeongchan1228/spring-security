package oauth2.oauth2study.service;

import lombok.RequiredArgsConstructor;
import oauth2.oauth2study.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public boolean existsByEmail(final String email) {
        return userRepository.existsByEmail(email);
    }
}
