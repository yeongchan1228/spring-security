package springStudy.springSecurity.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springStudy.springSecurity.entity.Role;
import springStudy.springSecurity.entity.User;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitUser {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.init();
    }

    @Component
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final BCryptPasswordEncoder encoder;

        @Transactional
        public void init(){
            User user = User.createUser()
                    .username("user")
                    .password(encoder.encode("1234"))
                    .email("user@naver.com")
                    .role(Role.ROLE_USER)
                    .build();

            User manager = User.createUser()
                    .username("manager")
                    .password(encoder.encode("1234"))
                    .email("manager@naver.com")
                    .role(Role.ROLE_MANAGER)
                    .build();

            User admin = User.createUser()
                    .username("admin")
                    .password(encoder.encode("1234"))
                    .email("admin@naver.com")
                    .role(Role.ROLE_ADMIN)
                    .build();

            em.persist(user);
            em.persist(manager);
            em.persist(admin);
        }
    }
}
