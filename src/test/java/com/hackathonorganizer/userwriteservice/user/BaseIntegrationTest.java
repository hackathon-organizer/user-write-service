package com.hackathonorganizer.userwriteservice.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathonorganizer.userwriteservice.UserWriteServiceApplication;
import com.hackathonorganizer.userwriteservice.user.keycloak.Role;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = UserWriteServiceApplication.class)
@Slf4j
public abstract class BaseIntegrationTest {

    private static final PostgreSQLContainer postgresContainer;
    private static final KeycloakContainer keycloakContainer;

    private final String BASE_URL = "/api/v1/write/users/";

    static {
        postgresContainer = (PostgreSQLContainer) new PostgreSQLContainer(
                "postgres:14.4")
                .withReuse(true);
        postgresContainer.start();

        keycloakContainer = new KeycloakContainer(
                "quay.io/keycloak/keycloak:16.0.0")
                .withRealmImportFile("/realm-export.json");
        keycloakContainer.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @DynamicPropertySource
    public static void setDatasourceProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.driverClassName", postgresContainer::getDriverClassName);

        registry.add("keycloak.authUrl", keycloakContainer::getAuthServerUrl);
        registry.add("keycloak.username", keycloakContainer::getAdminUsername);
        registry.add("keycloak.password", keycloakContainer::getAdminPassword);

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/hackathon-organizer");
    }

    protected String getJaneDoeBearer(Role role) {

        try {
            MultiValueMap<String, String> formData = new HttpHeaders();
            formData.put("grant_type", Collections.singletonList("password"));
            formData.put("client_id", Collections.singletonList("hackathon-organizer-client"));
            formData.put("username", Collections.singletonList("janedoe_" + role.name()));
            formData.put("password", Collections.singletonList("qwerty"));

            String result = restTemplate.postForObject(
                    keycloakContainer.getAuthServerUrl() + "/realms/hackathon-organizer/protocol/openid-connect/token", formData, String.class);

            JacksonJsonParser jsonParser = new JacksonJsonParser();

            return jsonParser.parseMap(result)
                    .get("access_token")
                    .toString();
        } catch (Exception e) {
            log.error("Can't obtain an access token from Keycloak!", e);
        }
        return null;
    }

    protected MockHttpServletRequestBuilder postJsonRequest(Object body, String token,
                                                                     String... urlVariables) throws Exception {
        return post(BASE_URL + String.join("/", urlVariables))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
    }

    protected MockHttpServletRequestBuilder putJsonRequest(Object body, String token,
                                                                    String... urlVariables) throws Exception {
        return put(BASE_URL + String.join("/", urlVariables))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
    }

    protected MockHttpServletRequestBuilder patchJsonRequest(Object body, String token,
                                                                      String... urlVariables) throws Exception {
        return patch(BASE_URL + String.join("/", urlVariables))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
    }

    protected MockHttpServletRequestBuilder deleteJsonRequest(String token, String... urlVariables) throws Exception {
        return delete(BASE_URL + String.join("/", urlVariables))
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);
    }
}
