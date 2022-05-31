package springStudy.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springStudy.jwt.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);
}
