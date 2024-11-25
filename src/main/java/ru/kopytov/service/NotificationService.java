package ru.kopytov.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kopytov.dto.TaskUpdateDtoKafka;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

    public void notificationAboutUpdate(List<TaskUpdateDtoKafka> message) {
        log.info("Notification about update {}", message);
    }
}
