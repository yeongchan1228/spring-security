package springStudy.springSecurity.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springStudy.springSecurity.config.auth.PrincipalDetails;
import springStudy.springSecurity.entity.User;
import springStudy.springSecurity.repository.UserRepository;

@Controller // View를 리턴
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // localhost:8080
    // localhost:8080/
    @GetMapping({"", "/"})
    public String index(){
        return "index";
    }

    @GetMapping("/test/login")
    @ResponseBody
    public String loginTest(Authentication authentication,
                            @AuthenticationPrincipal PrincipalDetails userDetails){
//        System.out.println("============test");
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        System.out.println("authentication.getPrincipal() = " + principalDetails.getUser());
//
//        System.out.println("userDetails.getUsername() = " + userDetails.getUser());
        return "세션 정보 확인하기";

    }

    @GetMapping("/test/oauth/login")
    @ResponseBody
    public String loginOAuthTest(Authentication authentication,
                                 @AuthenticationPrincipal OAuth2User oAuth2User){
//        System.out.println("============OAuth test");
//        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
//        System.out.println("principal.getAuthorities() = " + principal.getAttributes());
//
//        System.out.println("oAuth2User = " + oAuth2User.getAttributes());

        return "세션 Oauth 정보 확인하기";

    }

    @GetMapping("/user")
    @ResponseBody
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("username = " + principalDetails.getUser().getUsername());
        return "user";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    @ResponseBody
    public String manager(){
        return "manager";
    }

    // 스프링 시큐리티가 해당 login을 낚아챔 -> securityConfig 파일을 생성하면 낚아채지 않음
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        String password = user.getPassword();
        String encPwd = passwordEncoder.encode(password);
        user.setPassword(encPwd);
        User saveUser = userRepository.save(user); // 패스워드 암호화가 안 되었기 때문에 회원가입은 되나 시큐리티 로그인이 불가능함. -> SecurityConfig에서 설정을 해야 함
        log.info(saveUser.getUsername() + " " + saveUser.getPassword());
        return "redirect:/loginForm";
    }

    @GetMapping("/info")
    @ResponseBody
    @Secured("ROLE_ADMIN") // 특정 메서드에 접근 권한을 설정할 수 있음 -> 시큐리티 설정에서 @EnableGlobalMethodSecurity(securedEnabled = true) 설정 필요.
    public String info(){
        return "개인 정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public String data(){
        return "데이터 정보";
    }

}
