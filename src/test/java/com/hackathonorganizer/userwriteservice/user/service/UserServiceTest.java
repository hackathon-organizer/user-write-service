package com.hackathonorganizer.userwriteservice.user.service;

import com.hackathonorganizer.userwriteservice.exception.ScheduleException;
import com.hackathonorganizer.userwriteservice.exception.UserException;
import com.hackathonorganizer.userwriteservice.user.creator.TestDataUtils;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.keycloak.Role;
import com.hackathonorganizer.userwriteservice.user.model.AccountType;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.model.dto.*;
import com.hackathonorganizer.userwriteservice.user.repository.ScheduleEntryRepository;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import com.nimbusds.jwt.JWTParser;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.ResultActions;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keyCloakService;

    @Mock
    private Principal principal;

    @Mock
    private ScheduleEntryRepository scheduleEntryRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<ScheduleEntry> entryCaptor;

    private final User mockUser = TestDataUtils.buildMockUser();

    @Test
    void shouldCreateUser() {
        //given
        String username = "username";
        doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(Mockito.any(User.class));

        //when
        userService.createUser("id", "username");

        //then
        verify(userRepository).save(userCaptor.capture());
        User user = userCaptor.getValue();

        assertThat(user.getUsername()).isEqualTo("username");
    }

    @Test
    void shouldUpdateUser() {
        //given
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));
        principal = new UserPrincipal("id");

        //when

        userService.updateUser(1L, new UserUpdateDto("new desc", new HashSet<>()), principal);

        //then
        verify(userRepository).findById(anyLong());
        verify(userRepository).save(userCaptor.capture());
        User user = userCaptor.getValue();

        assertThat(user.getDescription()).isEqualTo("new desc");
    }

    @Test
    void shouldThrowWhileUpdatingUser() {
        //given
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        //when

        Throwable throwable =
                catchThrowable(() -> userService.updateUser(1L,
                        new UserUpdateDto("new desc", new HashSet<>()), principal));

        //then
        verify(userRepository).findById(anyLong());
        assertThat(throwable).isExactlyInstanceOf(UserException.class);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhileEditingUser() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when

        Throwable thrown = catchThrowable(() -> userService.blockUser(1L, principal));

        //then
        verify(userRepository).findById(anyLong());
        assertThat(thrown).isExactlyInstanceOf(UserException.class);
    }

    @Test
    void shouldBlockUser() {
        //given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(Mockito.any(User.class));
        doAnswer(invocationOnMock -> new UserRepresentation()).when(keyCloakService).blockInKeycloak(Mockito.any(User.class));
        principal = new UserPrincipal("id");

        //when
        userService.blockUser(1L, principal);

        //then
        assertThat(userRepository.findById(1L).get().isBlocked()).isTrue();
    }

    @Test
    void shouldThrowUserExceptionWhileBlockingUser() {
        //given
        principal = new UserPrincipal("not a keycloak id");

        //when

        Throwable thrown = catchThrowable(() -> userService.blockUser(1L, principal));

        //then
        verify(userRepository).findById(anyLong());
        assertThat(thrown).isExactlyInstanceOf(UserException.class);

    }

    @Test
    void shouldUpdateUserMembership() {
        //given
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(userRepository).save(any(User.class));
        principal = new UserPrincipal("id");

        UserMembershipRequest request =  new UserMembershipRequest(1L, 15L, 33L);

        //when

        userService
                .updateUserHackathonMembership(1L, request, principal);

        //then
        verify(userRepository).findById(anyLong());
        verify(userRepository).save(userCaptor.capture());
        User user = userCaptor.getValue();

        assertThat(user.getCurrentTeamId()).isEqualTo(request.currentTeamId());
        assertThat(user.getCurrentHackathonId()).isEqualTo(request.currentHackathonId());
    }

    @Test
    void shouldCreateScheduleEntry() {
        //given
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0))
                .when(scheduleEntryRepository).save(any(ScheduleEntry.class));
        principal = new UserPrincipal("id");

        ScheduleEntryRequest request = TestDataUtils.buildScheduleEntryRequest();

        //when

        ScheduleEntryResponse response = userService
                .createUserScheduleEntry(mockUser.getId(), request, principal);

        //then
        verify(userRepository).findById(anyLong());
        verify(scheduleEntryRepository).save(entryCaptor.capture());
        ScheduleEntry entry = entryCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.hackathonId()).isEqualTo(request.hackathonId());
        assertThat(response.sessionStart()).isEqualTo(request.sessionStart());
        assertThat(entry.getSessionStart()).isEqualTo(request.sessionStart());
        assertThat(entry.getSessionEnd()).isEqualTo(request.sessionEnd());
    }

    @Test
    void shouldUpdateScheduleEntry() {
        //given
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockUser));
        when(scheduleEntryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestDataUtils.buildScheduleEntry(mockUser)));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0))
                .when(scheduleEntryRepository).save(any(ScheduleEntry.class));
        principal = new UserPrincipal("id");

        ScheduleEntryRequest scheduleEntryRequest = new ScheduleEntryRequest(
                1L,
                7L,
                "new info",
                "blue",
                OffsetDateTime.of(2124, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2124, 10, 10, 16, 0, 0, 0, ZoneOffset.UTC)
        );

        List<ScheduleEntryRequest> request = List.of(scheduleEntryRequest);

        //when

        userService.updateUserScheduleEntries(mockUser.getId(), request, principal);

        //then
        verify(userRepository).findById(anyLong());
        verify(scheduleEntryRepository).save(entryCaptor.capture());
        ScheduleEntry entry = entryCaptor.getValue();

        assertThat(entry.getSessionStart()).isEqualTo(scheduleEntryRequest.sessionStart());
        assertThat(entry.getSessionEnd()).isEqualTo(scheduleEntryRequest.sessionEnd());
        assertThat(entry.getInfo()).isEqualTo(scheduleEntryRequest.info());
        assertThat(entry.getEntryColor()).isEqualTo(scheduleEntryRequest.entryColor());
    }

    @Test
    void shouldUpdateScheduleEntryTime() {
        //given
        when(scheduleEntryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestDataUtils.buildScheduleEntry(mockUser)));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0))
                .when(scheduleEntryRepository).save(any(ScheduleEntry.class));
        principal = new UserPrincipal("id");

        ScheduleEntryRequest scheduleEntryRequest = new ScheduleEntryRequest(
                1L,
                7L,
                "new info",
                "blue",
                OffsetDateTime.of(2124, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2124, 10, 10, 16, 0, 0, 0, ZoneOffset.UTC)
        );

        //when

        userService.updateUserScheduleEntryTime(mockUser.getId(), scheduleEntryRequest, principal);

        //then
        verify(scheduleEntryRepository).save(entryCaptor.capture());
        ScheduleEntry entry = entryCaptor.getValue();

        assertThat(entry.getSessionStart()).isEqualTo(scheduleEntryRequest.sessionStart());
        assertThat(entry.getSessionEnd()).isEqualTo(scheduleEntryRequest.sessionEnd());
    }

    @Test
    void shouldDeleteScheduleEntry() {
        //given
        ScheduleEntry scheduleEntry = TestDataUtils.buildScheduleEntry(mockUser);

        when(scheduleEntryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(scheduleEntry));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0))
                .when(scheduleEntryRepository).deleteById(scheduleEntry.getId());
        principal = new UserPrincipal("id");

        //when

        userService.deleteScheduleEntry(mockUser.getId(), scheduleEntry.getId(), principal);

        //then
        verify(scheduleEntryRepository).deleteById(scheduleEntry.getId());
    }

    @Test
    void shouldThrowNotFoundWhenDeleteScheduleEntry() {
        //given
        ScheduleEntry scheduleEntry = TestDataUtils.buildScheduleEntry(mockUser);

        when(scheduleEntryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        principal = new UserPrincipal("id");

        //when

        Throwable throwable =
                catchThrowable(() -> userService.deleteScheduleEntry(mockUser.getId(), 999L, principal));

        //then
        assertThat(throwable).isExactlyInstanceOf(ScheduleException.class);
    }
}