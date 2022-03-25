package springStudy.springSecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springStudy.springSecurity.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);
}
