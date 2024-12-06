package ru.kopytov;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kopytov.service.AbstractContainerBaseTest;

@SpringBootTest
class MainTest extends AbstractContainerBaseTest {
    @Test
    @DisplayName("Тест загрузки контекста")
    void contextLoads(){
    }
}