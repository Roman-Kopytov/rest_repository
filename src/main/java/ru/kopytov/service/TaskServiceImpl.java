package ru.kopytov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kopytov.aspect.HandlingResult;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.dto.TaskMapper;
import ru.kopytov.dto.TaskUpdateDtoKafka;
import ru.kopytov.exception.NoEntityException;
import ru.kopytov.kafka.KafkaTaskProducer;
import ru.kopytov.model.Task;
import ru.kopytov.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements BaseTaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final KafkaTaskProducer kafkaTaskProducer;
    @Value("${spring.kafka.topic}")
    private String topic;

    @Override
    public TaskDto saveTask(TaskDto taskDto) {
        Task task = taskMapper.toTask(taskDto);
        TaskDto savedTask = taskMapper.toDto(taskRepository.save(task));
        return savedTask;
    }

    @Override
    public void deleteTask(long id) {
        getTaskFromRepository(id);
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto getTaskById(long id) {
        return taskMapper.toDto(getTaskFromRepository(id));
    }

    @Override
    public TaskDto updateTask(TaskDto taskDto) {
        Task savedTask = getTaskFromRepository(taskDto.getId());
        savedTask.setDescription(taskDto.getDescription());
        savedTask.setTitle(taskDto.getTitle());
        savedTask.setStatus(taskDto.getStatus());
        TaskDto updatedTask = taskMapper.toDto(taskRepository.save(savedTask));
        TaskUpdateDtoKafka dto = new TaskUpdateDtoKafka(updatedTask.getId(), updatedTask.getStatus());
        kafkaTaskProducer.send(topic, dto);
        return updatedTask;
    }

    @Override
    @HandlingResult
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(t -> taskMapper.toDto(t))
                .toList();
    }

    private Task getTaskFromRepository(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoEntityException("No entity found with id: " + id));
    }
}
