package com.hansenvillage.hansenapp;

import com.hansenvillage.hansenapp.dto.LoginRequest;
import com.hansenvillage.hansenapp.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseE2ETest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate testRestTemplate;
    protected HttpHeaders authenticatedHeaders;

    @SuppressWarnings("unused")
    protected void loginAs(String email, String password) {
        String loginUrl = "http://localhost:" + port + "/api/auth/login";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(loginUrl, loginRequest, LoginResponse.class);

        LoginResponse body = Objects.requireNonNull(loginResponse.getBody(), "response body /login is null");
        String accessToken = loginResponse.getBody().getAccessToken();

        this.authenticatedHeaders = new HttpHeaders();
        this.authenticatedHeaders.setContentType(MediaType.APPLICATION_JSON);
        this.authenticatedHeaders.setBearerAuth(accessToken);
    }
}