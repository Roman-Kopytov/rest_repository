package ru.kopytov.service;

import ru.kopytov.dto.TaskDto;

import java.util.List;

public interface BaseTaskService {

    TaskDto saveTask(TaskDto task);

    void deleteTask(long id);

    TaskDto getTaskById(long id);

    TaskDto updateTask(TaskDto taskDto);

    List<TaskDto> getAllTasks();

}
