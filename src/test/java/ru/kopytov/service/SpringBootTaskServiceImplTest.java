package ru.kopytov.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.kafka.KafkaTaskProducer;
import ru.kopytov.model.Task;
import ru.kopytov.model.TaskStatus;
import ru.kopytov.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SpringBootTaskServiceImplTest  extends AbstractContainerBaseTest{

    private final TaskServiceImpl taskService;

    private final TaskRepository taskRepository;

    @MockBean(name = "kafkaTaskProducer")
    private KafkaTaskProducer kafkaTaskProducer;
    private Task task;
    private Task secondTask;


    @BeforeEach
    void setUp() {
        taskRepository.truncateTable();
        task = new Task();
        task.setTitle("TestTitle");
        task.setUserId(1L);
        task.setDescription("TestDescription");
        task.setStatus(TaskStatus.ACTIVE);

        secondTask = new Task();
        secondTask.setTitle("TestTitle");
        secondTask.setUserId(1L);
        secondTask.setDescription("TestDescription");
        secondTask.setStatus(TaskStatus.IN_PROGRESS);

        taskRepository.save(task);
        taskRepository.save(secondTask);
    }


    @Test
    void saveTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle(task.getTitle());
        taskDto.setUserId(task.getUserId());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());

        TaskDto actual = taskService.saveTask(taskDto);

        TaskDto expected = new TaskDto();
        expected.setId(3L);
        expected.setTitle(task.getTitle());
        expected.setUserId(task.getUserId());
        expected.setDescription(task.getDescription());
        expected.setStatus(task.getStatus());

        assertEquals(expected, actual);
    }

    @Test
    void deleteTask() {
        taskService.deleteTask(task.getId());

        List<Task> all = taskRepository.findAll();
        Optional<Task> byId = taskRepository.findById(task.getId());
        assertEquals(1, all.size());
        assertFalse(byId.isPresent());
    }

    @Test
    void getTaskById() {
        TaskDto actual = taskService.getTaskById(task.getId());

        TaskDto expected = new TaskDto();
        expected.setId(task.getId());
        expected.setTitle(task.getTitle());
        expected.setUserId(task.getUserId());
        expected.setDescription(task.getDescription());
        expected.setStatus(task.getStatus());

        assertEquals(expected, actual);
    }

    @Test
    void updateTask() {
        doNothing().when(kafkaTaskProducer).send(any(),any());
        TaskDto expected = new TaskDto();

        expected.setId(task.getId());
        expected.setTitle("updateTitle");
        expected.setUserId(task.getUserId());
        expected.setDescription("updateDescription");
        expected.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto actual = taskService.updateTask(expected);

        assertEquals(expected, actual);

    }

    @Test
    void getAllTasks() {
        List<TaskDto> allTasks = taskService.getAllTasks();

        assertEquals(2, allTasks.size());
    }
}