package springStudy.jwt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;
import springStudy.jwt.filter.jwt.JwtAuthenticationFilter;
import springStudy.jwt.filter.jwt.JwtTemporaryTokenFilter;

//@CrossOrigin 인증이 필요한 모든 요청은 해결 X
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(new JwtTemporaryTokenFilter(), SecurityContextPersistenceFilter.class);
//        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class); // 내가 만든 필터 3이 모든 필터보다 가장 먼저 실행된다.
//        http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class); // 시큐리티 필터 체인이 우리가 등록한 필터보다 먼저 실행된다.
        /**
         * 이렇게 설정하면 내가 세션을 사용하고 있지 않으므로
         * 인증이 필요하지 않은 사이트는 전부 통과된다.
         * 따라서 jwt를 통한 인증으로 다른 요청을 거부해야 한다.
         * 내가 설정한 cors 필터를 사용함으로써 다른 react, view.js... 같은 곳에서 자바스크립트 요청이 와도 허용한다.
         */

        // 기본적인 http 로그인 방식을 사용 X
        // Bearer 방식을 사용하기 위해 설정
        http.csrf().disable()
                // 폼 로그인 사용 X -> jwt 서버이기 때문에 필요가 없음 -> 폼 태그를 만들어서 로그인 X
                .formLogin().disable()
                // 헤더에 유저 이름과 비밀번호를 전달하는 방식으로 사용 X -> http 방식만 사용해서 쿠키를 인증하기 때문에
                // 자바스크립트 같은 곳에서(출처가 다른 곳에서) 요청하는 것을 거부(CORS 정책)한다. 이런 이유 때문에 httpBasic을 사용해서
                // 요청 시 마다 Authorization 영역에 아이디랑 비밀번호를 같이 전달하는데 이 방식은 매우 안좋다(서버는 https를 사용해야 한다.)
                // -> 쿠키, 세션, jwt를 사용하는 의미가 없어짐.
                // 따라서 우리는 Authorization 영역에 토큰(jwt)를 넣어서 인증한다. -> 이 방식이 Bearer 방식 -> 노출이 되어도 조금 안전
                .httpBasic().disable()
                // UsernamePasswordAuthenticationFilter 동작하게 하기 위한 필터 추가
                // 파라미터로 AuthenticationManager를 넘겨야 한다.
                // AuthenticationManager가 로그인을 진행하는 객체
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);// 스프링 시큐리티가 세션을 생성하지 않겠다.

                http
                // @CrossOrigin (인증X), 시큐리티 필터에 등록 (인증 O)
                .addFilter(corsFilter) // 모든 요청이 필터를 거치는데 현재 설정된 필터는 cors 요청을 모두 허용한다.

                .authorizeRequests()
                .antMatchers("/api/v1/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();

    }
}
