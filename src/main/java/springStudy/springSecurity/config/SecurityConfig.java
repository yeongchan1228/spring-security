package springStudy.springSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 웹 시큐리티 활성 -> 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터 체인에 등록된다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder encodePwd(){ // 패스워드 자동 암호화 빈 등록
        return new BCryptPasswordEncoder(); // 해싱 함수를 통해 비밀번호를 암호화 해준다. -> 스프링 시큐리티에서 제공한다.
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception { // http 스프링 시큐리티 설정
        http.csrf().disable(); // 위조 요청을 막기 위한 것으로 활성화 되면 crsf 토큰도 같이 보내야 한다.
        http.authorizeRequests() // 요청 권한 설정
                .antMatchers("/user/**").authenticated() // /user 포함 아래의 모든 요청은 인증이 필요하다.
                .antMatchers("/manager/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll() // 다른 주소는 모두 허용
                .and()
                .formLogin()
                .loginPage("/loginForm") // user, manager, admin 방향의 접속은 /login으로 이동시킨다.
                .loginProcessingUrl("/login"); // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행함.
    }
}
