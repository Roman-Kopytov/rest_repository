package ru.kopytov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kopytov.model.Role;
import ru.kopytov.model.RoleEnum;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum roleEnum);
}
