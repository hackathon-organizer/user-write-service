package com.teamsfinder.userwriteservice.user.service;

import com.hackathonorganizer.userwriteservice.exception.KeycloakException;
import com.hackathonorganizer.userwriteservice.exception.UserException;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.model.AccountType;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.model.dto.UserEditDto;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import com.hackathonorganizer.userwriteservice.user.service.UserService;
import com.teamsfinder.userwriteservice.user.UnitBaseClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


class UserServiceTest extends UnitBaseClass {

    private static final String USER_KEYCLOAK_ID = "KEYCLOAKID";
    private static final String EDIT_STRING = "EDITED";
    private static final String USER_GITHUB = "GITHUB";
    private static final String USER_PICTURE = "PICTURE";
    private static final String USERNAME = "USERNAME";

    @Mock
    private UserRepository userRepository;
    @Mock
    private KeycloakService keyCloakService;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserService underTest;

    private final User testUser = User.builder()
            .id(1L)
            .keyCloakId(USER_KEYCLOAK_ID)
            .accountType(AccountType.USER)
            .githubProfileUrl(USER_GITHUB)
            .profilePictureUrl(USER_PICTURE)
            .blocked(false)
            .tags(new HashSet<>())
            .build();

    @Test
    void shouldCreateUser() {
        //given
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));

        //when
        underTest.createUser(USER_KEYCLOAK_ID, USERNAME);

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void shouldEditUser() {
        //given
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));

        //when

        underTest.editUser(1L, new UserEditDto(EDIT_STRING, new HashSet<>()), principal);

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        assertThat(userRepository.existsById(1L)).isTrue();
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhileEditingUser() {
        //given
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        //when
        Executable executableEditUser =
                () -> underTest.editUser(1L, new UserEditDto(EDIT_STRING, Set.of()), principal);

        //then
        assertThrows(UserException.class, executableEditUser);
    }

    @Test
    void shouldBlockUser() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));
        doAnswer(invocationOnMock -> new UserRepresentation()).when(keyCloakService).blockInKeycloak(Mockito.any(User.class));

        //when
        underTest.blockUser(1L, principal);

        //then
        assertThat(userRepository.findById(1L).get().isBlocked()).isTrue();
    }

    @Test
    void shouldThrowKeyCloakExceptionWhileBlockingUser() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Mockito.doThrow(new KeycloakException("Test throw error", HttpStatus.FORBIDDEN))
                .when(keyCloakService).blockInKeycloak(Mockito.any(User.class));

        //when
        Executable executableBlockUser = () -> underTest.blockUser(1L, principal);

        //then
        assertThrows(KeycloakException.class, executableBlockUser);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhileBlockingUser() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        //when
        Executable executableBlockUser = () -> underTest.blockUser(1L, principal);

        //then
        assertThrows(UserException.class, executableBlockUser);
    }
}