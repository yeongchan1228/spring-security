package oauth2.oauth2study.service.oauth2;

import lombok.RequiredArgsConstructor;
import oauth2.oauth2study.service.UserResgistrationService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacebookOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserResgistrationService userResgistrationService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final DefaultOAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();

        final OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String name = oAuth2User.getAttributes().get("name").toString();
        String email = oAuth2User.getAttributes().get("email").toString();

        userResgistrationService.requestRegistration(name, email);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                oAuth2User.getAttributes(),
                "id"
        );
    }
}
