package ru.kopytov.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kopytov.dto.TaskUpdateDtoKafka;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskProducer {
    private final KafkaTemplate kafkaTemplate;

    public void send(String topic, TaskUpdateDtoKafka message) {
        try {
            kafkaTemplate.send(topic, message);
            kafkaTemplate.flush();
        } catch (Exception e) {
            log.error("Error while sending message to topic: {}", topic, e);
        }
    }
}
