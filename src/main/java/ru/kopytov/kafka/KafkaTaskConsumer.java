package ru.kopytov.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.kopytov.dto.TaskUpdateDtoKafka;
import ru.kopytov.service.NotificationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskConsumer {
    private final NotificationService notificationService;

    @KafkaListener(id = "${spring.kafka.group-id}",
            topics = "${spring.kafka.topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listener(@Payload List<TaskUpdateDtoKafka> messageList, Acknowledgment ack) {
        log.debug("Task consumer started to process message");
        try {
            notificationService.notificationAboutUpdate(messageList);
        } catch (Exception e) {
            log.error("Error while processing Kafka message: {}", messageList, e);
        }
        ack.acknowledge();
        log.debug("Task consumer finished processing message");
    }
}
