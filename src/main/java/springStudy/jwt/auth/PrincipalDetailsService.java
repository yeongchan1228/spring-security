package springStudy.jwt.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springStudy.jwt.entity.User;
import springStudy.jwt.repository.UserRepository;

/**
 * http://localhost:8080/login 요청이 오면 실행된다.
 * 시큐리티 설정에서 formLogin().disable()로 해놓았기 떄문에 http://localhost:8080/login에서 처리 불가
 *
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findUser = userRepository.findByUsername(username);

        if(findUser != null){
            return new PrincipalDetails(findUser);
        }
        return null;
    }
}
