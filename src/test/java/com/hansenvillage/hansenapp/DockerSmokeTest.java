package com.hansenvillage.hansenapp;

import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DockerSmokeTest {
    @Test
    void dockerWorks() {
        assertTrue(DockerClientFactory.instance().isDockerAvailable());
    }
}