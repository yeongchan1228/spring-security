package springStudy.jwt.filter.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 스프링 시큐리티의 UsernamePasswordAuthenticationFilter
 * /login 요청해서 Post로 username과 password를 전송하면 해당 필터 동작
 * 시큐리티 설정의 formLogin().disable()로 인해 동작 X
 * 따라서 다시 등록을 해줘야 한다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    /**
     * 로그인 실행 시 실행되는 함수
     * /login 요청을 하면 로그인 시도를 위해 실행됨
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.out.println("로그인 시도 중 ");

        // 1번 username, password를 받아서

        // 2번 정상인지 로그인 시도
        // authenticationManager로 로그인 시도 시 -> PrincipleDetailsService가 실행된다. -> loadUserByUsername()이 실행됨

        // 3번 PrincipleDeatils를 세션에 담는다 -> 권한 관리를 위해서

        // 4번 JWT 토큰을 만들어 응답한다.
        return super.attemptAuthentication(request, response);
    }
}
