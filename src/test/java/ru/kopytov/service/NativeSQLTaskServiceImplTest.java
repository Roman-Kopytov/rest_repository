package ru.kopytov.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.dto.TaskMapper;
import ru.kopytov.kafka.KafkaTaskProducer;
import ru.kopytov.model.Task;
import ru.kopytov.model.TaskStatus;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class NativeSQLTaskServiceImplTest extends AbstractContainerBaseTest {

    private final TaskServiceImpl taskService;

    @PersistenceContext
    private EntityManager em;

    @MockBean(name = "kafkaTaskProducer")
    private KafkaTaskProducer kafkaTaskProducer;

    private Task task;
    private Task secondTask;

    private final TaskMapper taskMapper;


    @BeforeEach
    void setUp() {
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

        em.persist(task);
        em.persist(secondTask);
    }

    @AfterEach
    void tearDown() {
        em.createQuery("DELETE FROM Task").executeUpdate();
    }

    @Test
    void saveTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("NewTitle");
        taskDto.setUserId(2L);
        taskDto.setDescription("NewDescription");
        taskDto.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto savedTask = taskService.saveTask(taskDto);

        Task retrievedTask = em.createQuery("SELECT t FROM Task t WHERE t.id = :id", Task.class)
                .setParameter("id", savedTask.getId())
                .getSingleResult();

        assertThat(retrievedTask.getId(), notNullValue());
        assertThat(retrievedTask.getTitle(), equalTo(taskDto.getTitle()));
        assertThat(retrievedTask.getUserId(), equalTo(taskDto.getUserId()));
        assertThat(retrievedTask.getDescription(), equalTo(taskDto.getDescription()));
        assertThat(retrievedTask.getStatus(), equalTo(taskDto.getStatus()));
    }

    @Test
    void deleteTask() {
        taskService.deleteTask(task.getId());

        List<Task> remainingTasks = em.createQuery("SELECT t FROM Task t", Task.class).getResultList();
        Task deletedTask = em.createQuery("SELECT t FROM Task t WHERE t.id = :id", Task.class)
                .setParameter("id", task.getId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        assertThat(remainingTasks.size(), equalTo(1));
        assertThat(deletedTask, nullValue());
    }

    @Test
    void getTaskById() {
        TaskDto actualTask = taskService.getTaskById(task.getId());

        Task retrievedTask = em.createQuery("SELECT t FROM Task t WHERE t.id = :id", Task.class)
                .setParameter("id", task.getId())
                .getSingleResult();

        assertThat(actualTask.getId(), equalTo(retrievedTask.getId()));
        assertThat(actualTask.getTitle(), equalTo(retrievedTask.getTitle()));
        assertThat(actualTask.getUserId(), equalTo(retrievedTask.getUserId()));
        assertThat(actualTask.getDescription(), equalTo(retrievedTask.getDescription()));
        assertThat(actualTask.getStatus(), equalTo(retrievedTask.getStatus()));
    }

    @Test
    void updateTask() {
        doNothing().when(kafkaTaskProducer).send(ArgumentMatchers.any(), ArgumentMatchers.any());

        TaskDto updatedTaskDto = new TaskDto();
        updatedTaskDto.setId(task.getId());
        updatedTaskDto.setTitle("UpdatedTitle");
        updatedTaskDto.setUserId(task.getUserId());
        updatedTaskDto.setDescription("UpdatedDescription");
        updatedTaskDto.setStatus(TaskStatus.COMPLETED);

        taskService.updateTask(updatedTaskDto);

        Task retrievedTask = em.createQuery("SELECT t FROM Task t WHERE t.id = :id", Task.class)
                .setParameter("id", task.getId())
                .getSingleResult();

        assertThat(retrievedTask.getTitle(), equalTo(updatedTaskDto.getTitle()));
        assertThat(retrievedTask.getDescription(), equalTo(updatedTaskDto.getDescription()));
        assertThat(retrievedTask.getStatus(), equalTo(updatedTaskDto.getStatus()));
    }

    @Test
    void getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();

        List<Task> retrievedTasks = em.createQuery("SELECT t FROM Task t", Task.class).getResultList();

        assertThat(tasks.size(), equalTo(retrievedTasks.size()));
        assertThat(tasks.get(0), equalTo(taskMapper.toDto(retrievedTasks.get(0))));
        assertThat(tasks.get(1), equalTo(taskMapper.toDto(retrievedTasks.get(1))));
    }

}