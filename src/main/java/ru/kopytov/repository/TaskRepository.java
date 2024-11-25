package ru.kopytov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kopytov.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
