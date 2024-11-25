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
public class TaskUpdateDtoKafka {
    private long id;
    private TaskStatus status;
}
