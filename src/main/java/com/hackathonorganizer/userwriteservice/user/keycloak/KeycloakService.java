package com.hackathonorganizer.userwriteservice.user.keycloak;

import com.hackathonorganizer.userwriteservice.exception.KeycloakException;
import com.hackathonorganizer.userwriteservice.exception.UserException;
import com.hackathonorganizer.userwriteservice.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
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
    private final String REALM_NAME = "hackathon-organizer";

    public void blockInKeycloak(User user) {

        try {
            Keycloak keycloak = buildKeyCloak();
            UserResource userResource = getUserResource(user.getKeyCloakId(), keycloak);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            userRepresentation.setEnabled(false);
            userResource.update(userRepresentation);
        } catch (Exception exception) {
            throw new KeycloakException("Can't block user " + user.getId(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    public void updateUserRole(String keycloakId, Role newRole) {

        try {
            RealmResource realmResource = buildKeyCloak().realm(REALM_NAME);

            RolesResource realmRoles = realmResource.roles();
            RoleRepresentation userNewRole = realmRoles.list().stream().filter(role -> role.getName().equals(newRole.name())).findFirst()
                    .orElseThrow(() -> new UserException("Role " + newRole + " not found", HttpStatus.NOT_FOUND));

            UsersResource usersResource = realmResource.users();
            UserResource userResource = usersResource.get(keycloakId);
            RoleMappingResource roleMappingResource = userResource.roles();

            RoleScopeResource roleScopeResource = roleMappingResource.realmLevel();
            List<RoleRepresentation> rolesRepresentation = roleScopeResource.listAll();

            rolesRepresentation.add(userNewRole);
            userResource.roles().realmLevel().add(rolesRepresentation);

            log.info("Role {} added to user: {}", newRole, keycloakId);
        } catch (Exception ex) {
            log.info("Can't update user roles");

            throw new UserException("Can't update user roles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
