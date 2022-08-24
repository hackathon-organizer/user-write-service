package com.teamsfinder.userwriteservice.user.service;

import com.teamsfinder.userwriteservice.user.UnitBaseClass;
import com.teamsfinder.userwriteservice.user.dto.EditUserRequestDto;
import com.teamsfinder.userwriteservice.user.dto.UserResponseDto;
import com.teamsfinder.userwriteservice.user.exception.KeycloakException;
import com.teamsfinder.userwriteservice.user.exception.UserNotFoundException;
import com.teamsfinder.userwriteservice.user.keycloak.KeycloakService;
import com.teamsfinder.userwriteservice.user.model.AccountType;
import com.teamsfinder.userwriteservice.user.model.User;
import com.teamsfinder.userwriteservice.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
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

    @InjectMocks
    private UserService underTest;

    private User testUser = User.builder()
            .id(1L)
            .keyCloakId(USER_KEYCLOAK_ID)
            .accountType(AccountType.USER)
            .githubProfileUrl(USER_GITHUB)
            .profilePictureUrl(USER_PICTURE)
            .blocked(false)
            .tags(new ArrayList<>())
            .build();

    @Test
    void shouldCreateUser() {
        //given
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));

        //when
        UserResponseDto userDto = underTest.createUser(USER_KEYCLOAK_ID,
                USERNAME);

        //then
        assertThat(userDto.keyCloakId()).isEqualTo(USER_KEYCLOAK_ID);
        assertThat(userDto.username()).isEqualTo(USERNAME);
    }

    @Test
    void shouldEditUser() {
        //given
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));

        //when
        UserResponseDto userDto =
                underTest.editUser(new EditUserRequestDto(1L, EDIT_STRING,
                        EDIT_STRING, Set.of(), new ArrayList<>()));

        //then
        assertThat(userDto.id()).isEqualTo(1L);
        assertThat(userDto.keyCloakId()).isEqualTo(USER_KEYCLOAK_ID);
        assertThat(userDto.accountType()).isEqualTo(AccountType.USER);
        assertThat(userDto.githubProfileUrl()).isEqualTo(EDIT_STRING);
        assertThat(userDto.profilePictureUrl()).isEqualTo(EDIT_STRING);
        assertThat(userDto.blocked()).isEqualTo(false);
        assertThat(userDto.tags().size()).isEqualTo(0);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhileEditingUser() {
        //given
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        //when
        Executable executableEditUser =
                () -> underTest.editUser(new EditUserRequestDto(1L,
                        EDIT_STRING, EDIT_STRING, Set.of(), new ArrayList<>()));

        //then
        assertThrows(UserNotFoundException.class, executableEditUser);
    }

    @Test
    void shouldBlockUser() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));
        doAnswer(invocationOnMock -> new UserRepresentation()).when(keyCloakService).blockInKeycloak(Mockito.any(User.class));

        //when
        UserResponseDto userDto = underTest.blockUser(1L);

        //then
        assertThat(userDto.id()).isEqualTo(1L);
        assertThat(userDto.keyCloakId()).isEqualTo(USER_KEYCLOAK_ID);
        assertThat(userDto.accountType()).isEqualTo(AccountType.USER);
        assertThat(userDto.githubProfileUrl()).isEqualTo(USER_GITHUB);
        assertThat(userDto.profilePictureUrl()).isEqualTo(USER_PICTURE);
        assertThat(userDto.blocked()).isEqualTo(true);
        assertThat(userDto.tags().size()).isEqualTo(0);
    }

    @Test
    void shouldThrowKeyCloakExceptionWhileBlockingUser() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Mockito.doThrow(new KeycloakException("Test throw error")).when(keyCloakService).blockInKeycloak(Mockito.any(User.class));

        //when
        Executable executableBlockUser = () -> underTest.blockUser(1L);

        //then
        assertThrows(KeycloakException.class, executableBlockUser);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhileBlockingUser() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        //when
        Executable executableBlockUser = () -> underTest.blockUser(1L);

        //then
        assertThrows(UserNotFoundException.class, executableBlockUser);
    }
}