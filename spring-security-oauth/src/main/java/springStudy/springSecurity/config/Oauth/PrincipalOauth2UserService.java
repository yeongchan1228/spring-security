package springStudy.springSecurity.config.Oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import springStudy.springSecurity.config.Oauth.provider.FacebookUserInfo;
import springStudy.springSecurity.config.Oauth.provider.GoogleUserInfo;
import springStudy.springSecurity.config.Oauth.provider.NaverUserInfo;
import springStudy.springSecurity.config.Oauth.provider.OAuth2UserInfo;
import springStudy.springSecurity.config.PrincipalDetails;
import springStudy.springSecurity.entity.Role;
import springStudy.springSecurity.entity.User;
import springStudy.springSecurity.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    /**
     * 시큐리티 설정에서 .userService(userService)
     * 구글 로그인 후 받은 유저 정보 후 처리
     * 구글로 부터 받은 유저 요청 데이터에 대한 후처리 함수
     * userRequest -> 엑세스 토큰 + 사용자 프로필 정보
     * 함수 종료 시 @AuthenticationPrincipal 어노테이션이 생성된다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest.getClientRegistration() = " + userRequest.getClientRegistration()); // 어떤 Oauth로 로그인 했는지?
        System.out.println("userRequest.getAccessToken = " + userRequest.getAccessToken());

        // 구글 로그인 버튼 -> 구글 로그인 창 -> 로그인 완료 -> code를 리턴(Oauth-client 라이브러리가 받아줌) -> AccessToken 요청
        // 여기까지가 userRequest 정보 -> 이런 userRequest 정보를 바탕으로 회원 프로필을 받아야 한다.(super.loadUser() 함수를 통해) -> 회원 프로필
        // 즉, super.loadUser -> 회원 프로필을 받아주는 메서드
//        System.out.println("super.loadUser(userRequest).getAttributes() = " + super.loadUser(userRequest).getAttributes());
        /**
         * Map<String, Object> 형태로 저장되어 있다.
         * sub=103683745224940187287 -> 구글의 회원 가입한 아이디
         * name=최영찬, given_name=영찬, family_name=최
         * picture=https://lh3.googleusercontent.com/a/AATXAJySJgiIHeDHyasRRI2-UvzXmw1G8Dri_wPrFDOv=s96-c -> 프로필 사진
         * email=dud03265@gmail.com, email_verified=true, locale=ko
         *
         * 즉, 우리 User에 저장 내용
         * username = google_103683745224940187287 -> 중복될 일 X
         * password = 암호화해서 아무거나
         * email = dud03265@gmail.com
         * role = 'ROLE_USER'
         * provider = "google"
         * providerId = 103683745224940187287
         * 즉, super.loadUser(userRequest).getAttributes()을 토대로 강제 회원가입 시킨다.
         */
        OAuth2User oAuth2User = super.loadUser(userRequest); // 유저 정보 가져오기
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        // User로 후 처리 시작
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            GoogleUserInfo googleUserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

            PrincipalDetails createdGoogleUser = saveUser(oAuth2User, googleUserInfo);

            if (createdGoogleUser != null) return createdGoogleUser;

        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            FacebookUserInfo facebookUserInfo = new FacebookUserInfo(oAuth2User.getAttributes());

            PrincipalDetails createdFacebookUser = saveUser(oAuth2User, facebookUserInfo);

            if(createdFacebookUser != null) return createdFacebookUser;

        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            NaverUserInfo naverUserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));

            PrincipalDetails createdNaverUser = saveUser(oAuth2User, naverUserInfo);

            if(createdNaverUser != null)    return createdNaverUser;
        }

        return null;

    }

    private PrincipalDetails saveUser(OAuth2User oAuth2User, OAuth2UserInfo userInfo) {
        User findUser = userRepository.findByUsername(userInfo.getName());

        if (findUser == null) {

            User createdUser = User.createUser()
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .username(userInfo.getName())
                    .password(encoder.encode("겟인데어"))
                    .email(userInfo.getEmail())
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(createdUser);

            log.info(userInfo.getProvider() + " 사용자 생성 : " + userInfo.getName());

            return new PrincipalDetails(createdUser, oAuth2User.getAttributes());
        } else {
            log.info("이미 생성된 " + userInfo.getProvider() + " 사용자");
        }
        return null;
    }
}
