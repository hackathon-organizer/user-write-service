package com.hackathonorganizer.userwriteservice.user.keycloak;

import com.hackathonorganizer.userwriteservice.exception.KeycloakException;
import com.hackathonorganizer.userwriteservice.exception.UserException;
import com.hackathonorganizer.userwriteservice.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final KeycloakProperties keycloakProperties;

    public void blockInKeycloak(User user) {
        try {
            Keycloak keycloak = buildKeyCloak();
            UserResource userResource = getUserResource(user.getKeyCloakId(), keycloak);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            userRepresentation.setEnabled(false);
            userResource.update(userRepresentation);
        } catch (Exception exception) {
            throw new KeycloakException(exception.getMessage());
        }
    }

    private UserResource getUserResource(String keyCloakId, Keycloak keycloak) {
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        UsersResource usersResource = realmResource.users();
        return usersResource.get(keyCloakId);
    }

    public Keycloak buildKeyCloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthUrl())
                .realm(keycloakProperties.getMasterRealm())
                .clientId(keycloakProperties.getClientId())
                .username(keycloakProperties.getUsername())
                .password(keycloakProperties.getPassword())
                .build();
    }

    public void updateUserRoles(String keycloakId, Role newRole) {

        try {
            Keycloak keycloak = buildKeyCloak();
            String realm = "hackathon-organizer";

            UsersResource usersResource = keycloak.realm(realm).users();
            UserResource userResource = usersResource.get(keycloakId);

            List<RoleRepresentation> roles = userResource.roles().realmLevel().listEffective();
            RoleRepresentation foundedRole = userResource.roles().realmLevel().listAvailable()
                    .stream().filter(role -> role.getName().equals(newRole.name())).toList().get(0);

            roles.add(foundedRole);
            userResource.roles().realmLevel().add(roles);

            log.info("Role {} added to user: {}", newRole, keycloakId);
        } catch (Exception ex) {
            log.info("Can't update user roles");
            ex.printStackTrace();
            throw new UserException("Can't update user roles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
