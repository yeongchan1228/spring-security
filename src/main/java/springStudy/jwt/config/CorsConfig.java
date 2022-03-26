package springStudy.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * cros -> 교차 출처 리소스
 * Sop -> 다른 출처에 있는 리소스 사용 X
 * 다른 출처를 지닌 곳에서 리소스를 가져오는 것이 흔한데 모든 것을 막을 순 없으니 예외 정책 -> CROS
 * ex) https://evan-moon.github.io -> 리소스 요청 -> https://api.evan.com 은 호스트가 다르기 떄문에 요청 무시
 */
@Configuration
public class CorsConfig {

    // 스프링 시큐리티가 들고 있는 필터
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        /**
         * 내 서버가 응답을 할 때 JSON을 자바스크립트에서 처리할 수 있게 할지를 설정
         * 만약 false면 자바스크립트 요청은 무시된다.
         */
        config.setAllowCredentials(true);

        config.addAllowedOrigin("*"); // 모든 IP 응답 허용
        config.addAllowedHeader("*"); // 모든 헤더에 응답을 허용
        config.addAllowedMethod("*"); // 모든 post, get, delete, fetch 허용
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
