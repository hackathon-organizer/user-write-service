package com.hackathonorganizer.userwriteservice.user.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hackathonorganizer.userwriteservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KeycloakListener {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "keycloak.queue")
    public void handleUserCreateAccount(String data) throws JsonProcessingException {
        String keyCloakId = getKeyCloakId(data);
        String username = getUsername(data);
        userService.createUser(keyCloakId, username);
    }

    private String getUsername(String data) throws JsonProcessingException {
        ObjectNode node = objectMapper.readValue(data, ObjectNode.class);
        JsonNode usernameField = node.at("/details/username");
        return usernameField.asText();
    }

    private String getKeyCloakId(String data) throws JsonProcessingException {
        ObjectNode node = objectMapper.readValue(data, ObjectNode.class);
        JsonNode keyCloakIdField = node.get("userId");
        return keyCloakIdField.asText();
    }
}
