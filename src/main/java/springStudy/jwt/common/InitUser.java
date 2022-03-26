package springStudy.jwt.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springStudy.jwt.entity.Role;
import springStudy.jwt.entity.User;

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

        private final BCryptPasswordEncoder encoder;
        private final EntityManager em;

        @Transactional
        public void init(){
            User user = User.createUser()
                    .username("test")
                    .password(encoder.encode("1234"))
                    .role(Role.ROLE_USER)
                    .build();

            em.persist(user);
        }
    }
}
