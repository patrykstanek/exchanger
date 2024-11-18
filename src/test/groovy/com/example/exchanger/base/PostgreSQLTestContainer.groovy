package com.example.exchanger.base

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

class PostgreSQLTestContainer implements
    ApplicationContextInitializer<ConfigurableApplicationContext>, AfterAllCallback {

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>(
        "postgres:16.5")
        .withDatabaseName("postgres")
        .withUsername("postgres")
        .withPassword("postgres")

    @Override
    void initialize(ConfigurableApplicationContext applicationContext) {
        postgreSQLContainer.start()

        TestPropertyValues.of(
            "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
            "spring.datasource.username=" + postgreSQLContainer.getUsername(),
            "spring.datasource.password=" + postgreSQLContainer.getPassword()
        ).applyTo(applicationContext.getEnvironment())
    }

    @Override
    void afterAll(ExtensionContext context) throws Exception {
        if (postgreSQLContainer == null) {
            return
        }
        postgreSQLContainer.close()
    }

}
