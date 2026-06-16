package com.example.todoapi;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test — requires a running PostgreSQL instance.
 * Run manually with: mvn test -Dspring.datasource.url=jdbc:postgresql://localhost:5432/todoapi
 */
@SpringBootTest
class TodoApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
