package ru.kopytov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.kopytov.dto.TaskDto;
import ru.kopytov.dto.TaskMapper;
import ru.kopytov.model.Task;
import ru.kopytov.model.TaskStatus;
import ru.kopytov.repository.TaskRepository;
import ru.kopytov.service.AbstractContainerBaseTest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TaskControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EntityManager entityManager;

    private List<TaskDto> taskDtos;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    @Transactional
    void setUp() {
        taskRepository.truncateTable();
        taskDtos = generateTask();
    }


    private LinkedList<TaskDto> generateTask() {
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Test A");
        task1.setDescription("Test A");
        task1.setUserId(1L);
        task1.setStatus(TaskStatus.IN_PROGRESS);

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Test B");
        task2.setDescription("Test B");
        task2.setUserId(2L);
        task2.setStatus(TaskStatus.COMPLETED);

        task3 = new Task();
        task3.setId(3L);
        task3.setTitle("Test C");
        task3.setDescription("Test C");
        task3.setUserId(3L);
        task3.setStatus(TaskStatus.COMPLETED);
        List<Task> tasks = taskRepository.saveAll(Arrays.asList(task1, task2, task3));
        LinkedList<TaskDto> taskDtoList = new LinkedList<>();
        tasks.stream().map(taskMapper::toDto).forEach(taskDtoList::add);
        return taskDtoList;
    }

    @Test
    void shouldReturnSavedTask() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("NewTitle");
        taskDto.setUserId(4L);
        taskDto.setDescription("NewDescription");
        taskDto.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto responseTask = new TaskDto();
        responseTask.setId(4L);
        responseTask.setUserId(taskDto.getUserId());
        responseTask.setTitle(taskDto.getTitle());
        responseTask.setDescription(taskDto.getDescription());
        responseTask.setStatus(taskDto.getStatus());


        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseTask)));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        TaskDto responseTask = taskDtos.get(0);
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseTask)));
    }

    @Test
    void shouldReturnListOfTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(taskDtos)));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());

        List<Task> all = taskRepository.findAll();
        assertEquals(taskDtos.size()-1, all.size());
        assertEquals(List.of(task2, task3).toString(), all.toString());
    }

    @Test
    void shouldReturnUpdatedTask() throws Exception {
        TaskDto responseTask = new TaskDto();
        responseTask.setId(1L);
        responseTask.setUserId(1L);
        responseTask.setTitle("updatedTitle");
        responseTask.setDescription("updatedDescription");
        responseTask.setStatus(TaskStatus.ACTIVE);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(responseTask)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseTask)));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() throws Exception {
        mockMvc.perform(get("/tasks/4"))
                .andExpect(status().is4xxClientError());
    }
}