package com.github.vontikov.quarkus.jdbc.example;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    static PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    @Override
    public Map<String, String> start() {
        db.start();
        return Collections.singletonMap("datasource.url", db.getJdbcUrl());
    }

    @Override
    public void stop() {
        db.stop();
    }
}
