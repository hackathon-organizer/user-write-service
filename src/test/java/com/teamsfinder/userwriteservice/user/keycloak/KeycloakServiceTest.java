package com.teamsfinder.userwriteservice.user.keycloak;

import com.hackathonorganizer.userwriteservice.exception.KeycloakException;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakProperties;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.teamsfinder.userwriteservice.user.KeycloakIntegrationBaseClass;
import com.teamsfinder.userwriteservice.user.creator.UserCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertThrows;


class KeycloakServiceTest extends KeycloakIntegrationBaseClass {

    @Autowired
    private KeycloakService underTest;

    @Autowired
    private UserCreator userCreator;

    @Autowired
    private KeycloakProperties keycloakProperties;

    @Test
    void shouldThrowWhileBlockingInKeycloak() {
        //given
        User user = userCreator.create();

        //when
        Executable throwBlock = () -> underTest.blockInKeycloak(user);

        //then
        assertThrows(KeycloakException.class, throwBlock);
    }

    private User createUserInKeycloak() {

        Keycloak keycloak = underTest.buildKeyCloak();
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(createUserRepresentation());
        String userId = CreatedResponseUtil.getCreatedId(response);
        return userCreator.createWithKeycloakId(userId);
    }

    private UserRepresentation createUserRepresentation() {

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername("underTest");
        return userRepresentation;
    }
}