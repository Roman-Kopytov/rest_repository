package ru.kopytov.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.dto.TaskMapper;
import ru.kopytov.exception.NoEntityException;
import ru.kopytov.kafka.KafkaTaskProducer;
import ru.kopytov.model.Task;
import ru.kopytov.model.TaskStatus;
import ru.kopytov.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private final TaskMapper taskMapper = new TaskMapper();
    @Mock
    private KafkaTaskProducer kafkaTaskProducer;
    private Task task;
    private BaseTaskService taskService;


    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("TestTitle");
        task.setUserId(1L);
        task.setDescription("TestDescription");
        task.setStatus(TaskStatus.ACTIVE);
        taskService = new TaskServiceImpl(taskRepository, taskMapper, kafkaTaskProducer);

    }

    @Test
    @DisplayName("Тест сохранения задачи")
    void testSaveTask() {
        when(taskRepository.save(any())).thenReturn(task);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());

        TaskDto actual = taskService.saveTask(taskDto);
        assertNotNull(actual);
        assertEquals(taskDto, actual);

    }

    @Test
    @DisplayName("Тест получения задачи по Id")
    void testGetTaskById() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.ofNullable(task));
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());

        TaskDto actual = taskService.getTaskById(task.getId());
        assertNotNull(actual);
        assertEquals(taskDto, actual);
    }

    @Test
    @DisplayName("Тест ошибки при поулчении задачи")
    void testNotFoundTask() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoEntityException.class, () -> taskService.getTaskById(100L));
    }

    @Test
    void testUpdateTask() {
        TaskDto taskDto = new TaskDto();
        task.setDescription("updateDescription");
        task.setTitle("updateTitle");
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskDto.setId(task.getId());
        taskDto.setTitle("updateTitle");
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription("updateDescription");
        taskDto.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.save(task)).thenReturn(task);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.ofNullable(task));


        TaskDto actual = taskService.updateTask(taskDto);
        assertNotNull(actual);
        assertEquals(taskDto, actual);
    }

    @Test
    void testGetAllTasks() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());

        TaskDto taskDtoSecond = new TaskDto();
        taskDto.setId(2L);
        taskDto.setTitle(task.getTitle());
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());

        List<TaskDto> expected = new ArrayList<>();
        expected.add(taskDto);
        expected.add(taskDtoSecond);

        Task secondTask = new Task();
        secondTask.setId(2L);
        secondTask.setTitle("TestTitle");
        secondTask.setUserId(1L);
        secondTask.setDescription("TestDescription");
        secondTask.setStatus(TaskStatus.ACTIVE);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(secondTask);

        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskDto> actual = taskService.getAllTasks();

        assertEquals(expected.size(), actual.size());
        assertEquals(actual, taskService.getAllTasks());
    }
}