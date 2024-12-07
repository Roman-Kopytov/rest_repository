package ru.kopytov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kopytov.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String username);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

}
