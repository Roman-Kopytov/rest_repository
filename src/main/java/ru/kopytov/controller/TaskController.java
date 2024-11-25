package ru.kopytov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.kopytov.aspect.LogController;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.dto.TaskUpdateDtoKafka;
import ru.kopytov.kafka.KafkaTaskProducer;
import ru.kopytov.service.TaskServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskServiceImpl taskService;
    private final KafkaTaskProducer kafkaTaskProducer;
    @Value("${spring.kafka.topic}")
    private String topic;

    @PostMapping
    @LogController
    public TaskDto saveTask(@RequestBody TaskDto task) {
        return taskService.saveTask(task);
    }

    @GetMapping("/{id}")
    @LogController
    public TaskDto getTask(@PathVariable("id") long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    @LogController
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @DeleteMapping("/{id}")
    @LogController
    public void deleteTask(@PathVariable("id") long id) {
        taskService.deleteTask(id);
    }

    @PutMapping("/{id}")
    @LogController
    public TaskDto updateTask(@RequestBody TaskDto task, @PathVariable("id") long id) {
        task.setId(id);
        TaskDto updateTask = taskService.updateTask(task);
        TaskUpdateDtoKafka dto = new TaskUpdateDtoKafka(updateTask.getId(), updateTask.getStatus());
        kafkaTaskProducer.send(topic, dto);
        return updateTask;
    }

}