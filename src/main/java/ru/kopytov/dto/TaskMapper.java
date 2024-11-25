package ru.kopytov.dto;

import org.springframework.stereotype.Component;
import ru.kopytov.model.Task;

@Component
public class TaskMapper {

    public TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .description(task.getDescription())
                .title(task.getTitle())
                .userId(task.getUserId())
                .status(task.getStatus())
                .build();
    }

    public Task toTask(TaskDto dto) {
        return Task.builder()
                .description(dto.getDescription())
                .title(dto.getTitle())
                .userId(dto.getUserId())
                .status(dto.getStatus())
                .build();
    }
}
