package com.teamsfinder.userwriteservice.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathonorganizer.userwriteservice.UserWriteServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

@AutoConfigureMockMvc
@SpringBootTest(classes = UserWriteServiceApplication.class)
public abstract class IntegrationBaseClass {

    private static final PostgreSQLContainer postgresContainer;

    static {
        postgresContainer = (PostgreSQLContainer) new PostgreSQLContainer(
                "postgres:14.4")
                .withReuse(true);
        postgresContainer.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    public static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.password",
                postgresContainer::getPassword);
        registry.add("spring.datasource.username",
                postgresContainer::getUsername);
        registry.add("spring.datasource.driverClassName",
                postgresContainer::getDriverClassName);
    }
}
