package oauth2.oauth2study.config;

import lombok.AllArgsConstructor;
import oauth2.oauth2study.service.oauth2.FacebookOAuth2UserService;
import oauth2.oauth2study.service.oauth2.GoogleOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    private final Environment environment;
    private final GoogleOAuth2UserService googleOAuth2UserService;
    private final FacebookOAuth2UserService facebookOAuth2UserService;
    private final String registration = "spring.security.oauth2.client.registration.";

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/login", "/index")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository())
                        .authorizedClientService(authorizedClientService())
                        .userInfoEndpoint(
                                user -> user
                                        .oidcUserService(googleOAuth2UserService) // google 인증, OpenId Connect 1.0
                                        .userService(facebookOAuth2UserService) // facebook 인증, OAuth2 통신
                        )
                );

        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return  new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        return new InMemoryClientRegistrationRepository(Arrays.asList(
                googleClientRegistration(),
                facebookClientRegistration()
        ));
    }

    private ClientRegistration googleClientRegistration() {
        String clientId = environment.getProperty(registration + "google.client-id");
        String clientSecret = environment.getProperty(registration + "google.client-secret");

        return CommonOAuth2Provider
                .GOOGLE
                .getBuilder("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    private ClientRegistration facebookClientRegistration() {
        String clientId = environment.getProperty(registration + "facebook.client-id");
        String clientSecret = environment.getProperty(registration + "facebook.client-secret");

        return CommonOAuth2Provider
                .FACEBOOK
                .getBuilder("facebook")
                .scope("public_profile",
                        "email",
                        "user_gender",
                        "user_birthday")
                .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,picture,gender,birthday")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}
