package springStudy.jwt.filter.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import springStudy.jwt.auth.PrincipalDetails;
import springStudy.jwt.entity.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        try {
//            BufferedReader br = request.getReader();
//            String input = br.readLine();
//            System.out.println(input);
            ObjectMapper om = new ObjectMapper(); // Json으로 보낸 데이터만 받는다
            User user = om.readValue(request.getInputStream(), User.class); // JSon 받는 방식
            System.out.println("user = " + user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword()
            ); // 토큰 생성

            // PrincipalDetailsService의 loadUserByUsername() 실행됨
            // 인증을 함
            // 이때 authentication에 로그인 정보가 담긴다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("principalDetails.getUser() = " + principalDetails.getUser());

            // 정상적으로 완료되면 authentication 객체가 session 영역에 저장된다.
            // 굳이 authentication을 리턴하는 이유는 권한 관리를 시큐리티가 해주기 때문
            // 굳이 JWT 토큰을 이용하면서 세션을 만들 필요는 없는데 권한 관리 때문에 세션을 만들어 준다.
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("======================");
        return null; // 오류 시
    }

    /**
     * attemptAuthentication에서 인증이 정상적으로 완료되면 실행됨
     * jwt 토큰을 만들어서 request 응답한 사용자에게 jwt 토큰 응답
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response
            , FilterChain chain, Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", principalDetails.getUser().getId());
        payloads.put("username", principalDetails.getUser().getUsername());


        String jwtToken = Jwts.builder()
                .setSubject("cos토큰")
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .setClaims(payloads)
                .signWith(SignatureAlgorithm.HS512, JwtProperties.SECRET.getBytes())
                .compact();

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
