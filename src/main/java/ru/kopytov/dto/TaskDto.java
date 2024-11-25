package ru.kopytov.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kopytov.model.TaskStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {

    private long id;
    private String title;
    private String description;
    private long userId;
    private TaskStatus status;
}
