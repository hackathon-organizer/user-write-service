package com.hackathonorganizer.userwriteservice.user.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "keycloak")
@Component
@Getter
@Setter
public class KeycloakProperties {

    private String authUrl;
    private String realm;
    private String masterRealm;
    private String clientId;
    private String username;
    private String password;
}
