package ru.kopytov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.service.TaskServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskServiceImpl taskService;

    @PostMapping
    public TaskDto saveTask(@RequestBody TaskDto task) {
        return taskService.saveTask(task);
    }

    @GetMapping("/{id}")
    public TaskDto getTask(@PathVariable("id") long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @DeleteMapping("/{id}")

    public void deleteTask(@PathVariable("id") long id) {
        taskService.deleteTask(id);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@RequestBody TaskDto task, @PathVariable("id") long id) {
        task.setId(id);
        return taskService.updateTask(task);
    }

}