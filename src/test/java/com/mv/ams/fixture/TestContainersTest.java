package com.mv.ams.fixture;

import org.junit.jupiter.api.Tag;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Tag("IntegratedTest")
public abstract class TestContainersTest {

    static final PostgreSQLContainer<?> postgreSQLContainer
        = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.2"))
            .withDatabaseName("postgres")
            .withCopyFileToContainer(MountableFile
                .forClasspathResource("postgres/init.sql"), "/docker-entrypoint-initdb.d/")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void datasourceConfig(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }
}
