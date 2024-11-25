package ru.kopytov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kopytov.aspect.HandlingResult;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.dto.TaskMapper;
import ru.kopytov.exception.NoEntityException;
import ru.kopytov.model.Task;
import ru.kopytov.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements BaseTaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

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
        TaskDto updatedTask = taskMapper.toDto(taskRepository.save(savedTask));
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
