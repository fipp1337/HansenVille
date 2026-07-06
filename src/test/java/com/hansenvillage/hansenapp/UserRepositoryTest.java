package com.hansenvillage.hansenapp;

import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @Autowired
    UserRepository repository;

    @Test
    void shouldSaveUser() {
        User user = repository.save(new User());

        assertThat(user.getId()).isNotNull();
    }

    @Test
    void dockerTest() {
        try (var c = new org.testcontainers.containers.PostgreSQLContainer<>("postgres:16-alpine")) {
            c.start();
            System.out.println(c.getJdbcUrl());
        }
    }
}
