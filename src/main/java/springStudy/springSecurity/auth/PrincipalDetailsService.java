package springStudy.springSecurity.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springStudy.springSecurity.entity.User;
import springStudy.springSecurity.repository.UserRepository;

/**
 * 시큐리티 회원 로그인 설정 2
 * 시큐리티 설정에서 loginProcessingUrl("/login")
 * /login 요청이 오면 자동으로 UserDetailsService 타입으로 IOC되어 있는 loadUserByUsername이 실행됨
 * 로그인 페이지에서 받아오는 필드명이 username으로 동일해야 한다.
 * 만약 필드가 다르면 시큐리티 설정에서 .loginPage() 아래 .usernameparameter("필드명") 설정을 해야 한다.
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 시큐리티 session = Authentication = UserDetails(PrincipalDetails)
    // 해당 함수가 반환되면 Authentication 안에 들어간다.
    // 즉, 시큐리티 session(내부 Authentication(내부 PrincipalDetails))
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findUser = userRepository.findByUsername(username);

        if(findUser != null){ // 해당 유저가 있다.
            return new PrincipalDetails(findUser);
        }

        return null;
    }
}
