package ru.kopytov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.kopytov.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "TRUNCATE TABLE tasks RESTART IDENTITY CASCADE")
    void truncateTable();
}
