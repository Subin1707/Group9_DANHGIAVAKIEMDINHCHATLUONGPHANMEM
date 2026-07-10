package com.example.servingwebcontent.database;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClassPathResource schema = new ClassPathResource("schema.sql");
        String sql = new String(schema.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            for (String command : sql.split(";")) {
                String trimmed = command.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        }
    }
}
