package springStudy.springSecurity.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/user")
    public String user(){
        return "user";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("/manager")
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

}
