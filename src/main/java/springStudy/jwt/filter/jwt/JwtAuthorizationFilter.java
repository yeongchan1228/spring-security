package springStudy.jwt.filter.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import springStudy.jwt.auth.PrincipalDetails;
import springStudy.jwt.entity.User;
import springStudy.jwt.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Collection;


/**
 * 시큐리티가 filter를 가지고 있는데 그 필터 중 BasicAuthenticationFilter를 가지고 있다.
 * 권한이나 인증이 필요한 특정 주소를 요청 했을 때 위 필터를 무조건 타게 되어 있다.
 * 만약에 권한이나 인증이 필요한 주소가 아니면 위 필터를 안 탄다.
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        super.doFilterInternal(request, response, chain);

        System.out.println("인증이나 권한이 필요한 주소 요청이 옴");

        String jwtToken = request.getHeader("Authorization");
        System.out.println("jwtToken = " + jwtToken);

        // JWT 토큰 검증, 유효한지
        if(jwtToken == null || !jwtToken.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }

        jwtToken = jwtToken.replace("Bearer ", "");

        Claims body = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString("cos".getBytes()))
                .parseClaimsJws(jwtToken)
                .getBody();

        String username = body.get("username").toString();

        // 서명이 정상적으로 됨
        if(username != null){

            System.out.println("username = " + username);
            User findUser = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(findUser);

            System.out.println("principalDetails = " + principalDetails.getAuthorities());

            // Jwt 토큰 서명이 정상이면 Authentication 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities()); // 강제적으로 Authentication 객체를 만들어 준다.

            // 강제로 시큐리티 세션에 접근해서 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }
}
