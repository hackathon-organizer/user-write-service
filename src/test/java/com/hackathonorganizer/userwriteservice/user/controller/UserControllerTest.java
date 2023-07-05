package com.hackathonorganizer.userwriteservice.user.controller;

import com.hackathonorganizer.userwriteservice.user.creator.TestDataUtils;
import com.hackathonorganizer.userwriteservice.user.keycloak.Role;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.model.dto.ScheduleEntryRequest;
import com.hackathonorganizer.userwriteservice.user.model.dto.ScheduleMeetingRequest;
import com.hackathonorganizer.userwriteservice.user.model.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.user.model.dto.UserUpdateRequest;
import com.hackathonorganizer.userwriteservice.user.repository.ScheduleEntryRepository;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import com.hackathonorganizer.userwriteservice.user.BaseIntegrationTest;
import com.nimbusds.jwt.JWTParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private TestDataUtils testDataUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleEntryRepository scheduleEntryRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldEditUser() throws Exception {
        //given

        String token = getJaneDoeBearer(Role.USER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();

        User user = testDataUtils.createUserWithKeycloakId(keycloakId);
        UserUpdateRequest editUserDto = new UserUpdateRequest("new desc", Set.of());


        //when
        ResultActions resultActions =
                mockMvc.perform(patchJsonRequest(editUserDto, token, user.getId().toString()));

        //then
        resultActions.andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId()).get().getDescription()).isEqualTo(editUserDto.description());
    }

    @Test
    void shouldThrowWhileEditingUser() throws Exception {
        //given
        String token = getJaneDoeBearer(Role.USER);

        UserUpdateRequest editUserDto = new UserUpdateRequest("new desc", new HashSet<>());

        //when
        ResultActions resultActions = mockMvc.perform(patchJsonRequest(editUserDto, token, "5"));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUserMembership() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.USER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();

        User user = testDataUtils.createUserWithKeycloakId(keycloakId);

        UserMembershipRequest userMembershipRequest = new UserMembershipRequest(user.getId(), 5L, 20L);

        // when

        ResultActions resultActions =
                mockMvc.perform(patchJsonRequest(userMembershipRequest, token, user.getId().toString(), "membership"));

        // then
        resultActions.andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId()).get().getCurrentHackathonId())
                .isEqualTo(userMembershipRequest.currentHackathonId());
        assertThat(userRepository.findById(user.getId()).get().getCurrentTeamId())
                .isEqualTo(userMembershipRequest.currentTeamId());
    }

    @Test
    void shouldFailUserAuthorization() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.USER);
        String keycloakId = "Wrong token";

        User user = testDataUtils.createUserWithKeycloakId(keycloakId);

        UserMembershipRequest userMembershipRequest = new UserMembershipRequest(user.getId(), 5L, 20L);

        // when

        ResultActions resultActions =
                mockMvc.perform(patchJsonRequest(userMembershipRequest, token, user.getId().toString(), "membership"));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateUserScheduleEntry() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.ORGANIZER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();

        User user = testDataUtils.createUserWithKeycloakId(keycloakId);

        ScheduleEntryRequest scheduleEntryRequest = testDataUtils.buildScheduleEntryRequest();

        // when
        ResultActions resultActions =
                mockMvc.perform(postJsonRequest(scheduleEntryRequest, token, user.getId().toString(), "schedule"));

        // then
        resultActions.andExpect(status().isCreated());

        assertThat(scheduleEntryRepository.findAll().size()).isEqualTo(1);
        assertThat(scheduleEntryRepository.findAll().get(0).getHackathonId())
                .isEqualTo(scheduleEntryRequest.hackathonId());
        assertThat(scheduleEntryRepository.findAll().get(0).getSessionStart())
                .isEqualTo(scheduleEntryRequest.sessionStart());
        assertThat(userRepository.findUserByIdWithScheduleEntries(user.getId()).get().getScheduleEntries().size())
                .isEqualTo(1);
    }

    @Test
    void shouldUpdateScheduleEntry() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.ORGANIZER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();
        User user = testDataUtils.createUserWithKeycloakId(keycloakId);
        ScheduleEntry scheduleEntry = testDataUtils.createScheduleEntry(user);

        ScheduleEntryRequest scheduleEntryRequest = new ScheduleEntryRequest(
                scheduleEntry.getId(),
                5L,
                "info update",
                "red",
                OffsetDateTime.of(2123, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2123, 10, 10, 16, 0, 0, 0, ZoneOffset.UTC));

        List<ScheduleEntryRequest> request = List.of(scheduleEntryRequest);

        // when
        ResultActions resultActions =
                mockMvc.perform(putJsonRequest(request, token, user.getId().toString(), "schedule"));

        // then
        resultActions.andExpect(status().isOk());

        assertThat(scheduleEntryRepository.findById(scheduleEntry.getId()).get().getInfo())
                .isEqualTo(scheduleEntryRequest.info());
        assertThat(scheduleEntryRepository.findById(scheduleEntry.getId()).get().getSessionStart())
                .isEqualTo(scheduleEntryRequest.sessionStart());
        assertThat(scheduleEntryRepository.findById(scheduleEntry.getId()).get().getSessionEnd())
                .isEqualTo(scheduleEntryRequest.sessionEnd());
    }

    @Test
    void shouldUpdateScheduleEntryTime() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.ORGANIZER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();
        User user = testDataUtils.createUserWithKeycloakId(keycloakId);
        ScheduleEntry scheduleEntry = testDataUtils.createScheduleEntry(user);

        ScheduleEntryRequest scheduleEntryRequest = new ScheduleEntryRequest(
                scheduleEntry.getId(),
                5L,
                null,
                null,
                OffsetDateTime.of(2123, 10, 10, 14, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2123, 10, 10, 16, 0, 0, 0, ZoneOffset.UTC));

        // when
        ResultActions resultActions =
                mockMvc.perform(
                        patchJsonRequest(scheduleEntryRequest,
                                token,
                                user.getId().toString(),
                                "schedule",
                                scheduleEntry.getId().toString()));

        // then
        resultActions.andExpect(status().isOk());

        assertThat(scheduleEntryRepository.findById(scheduleEntry.getId()).get().getSessionStart())
                .isEqualTo(scheduleEntryRequest.sessionStart());
        assertThat(scheduleEntryRepository.findById(scheduleEntry.getId()).get().getSessionEnd())
                .isEqualTo(scheduleEntryRequest.sessionEnd());
    }

    @Test
    void shouldDeleteScheduleEntry() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.ORGANIZER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();
        User user = testDataUtils.createUserWithKeycloakId(keycloakId);
        ScheduleEntry scheduleEntry = testDataUtils.createScheduleEntry(user);

        // when
        ResultActions resultActions =
                mockMvc.perform(
                        deleteJsonRequest(token, user.getId().toString(), "schedule", scheduleEntry.getId().toString()));

        // then
        resultActions.andExpect(status().isOk());
        assertThat(scheduleEntryRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void shouldAssignTeamToMeetingWithMentor() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.TEAM_OWNER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();
        User user = testDataUtils.createUserWithKeycloakId(keycloakId);
        ScheduleEntry scheduleEntry = testDataUtils.createScheduleEntry(user);

        ScheduleMeetingRequest scheduleMeetingRequest = new ScheduleMeetingRequest(scheduleEntry.getTeamId(), user.getId());

        // when
        ResultActions resultActions =
                mockMvc.perform(
                        patchJsonRequest(scheduleMeetingRequest, token, "schedule", scheduleEntry.getId().toString(), "meeting"));

        // then
        resultActions.andExpect(status().isOk());

        assertThat(scheduleEntryRepository.findAll().size()).isEqualTo(1);
        assertThat(scheduleEntryRepository.findById(scheduleEntry.getId()).get().isAvailable()).isFalse();
    }

    @Test
    void shouldNotAssignTeamToMeetingWithMentor() throws Exception {
        // given
        String token = getJaneDoeBearer(Role.USER);
        String keycloakId = JWTParser.parse(token).getJWTClaimsSet().getSubject();
        User user = testDataUtils.createUserWithKeycloakId(keycloakId);
        ScheduleEntry scheduleEntry = testDataUtils.createScheduleEntry(user);
        scheduleEntry.setAvailable(false);
        testDataUtils.updateScheduleEntry(scheduleEntry);

        ScheduleMeetingRequest scheduleMeetingRequest = new ScheduleMeetingRequest(scheduleEntry.getTeamId(), user.getId());

        // when
        ResultActions resultActions =
                mockMvc.perform(
                        patchJsonRequest(scheduleMeetingRequest, token, "schedule", scheduleEntry.getId().toString(), "meeting"));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void shouldNotUpdateUserRole() throws Exception {
        // given
        String tokenOrganizer = getJaneDoeBearer(Role.ORGANIZER);
        String tokenUser = getJaneDoeBearer(Role.USER);
        String organizerKeycloakId = JWTParser.parse(tokenOrganizer).getJWTClaimsSet().getSubject();
        String userKeycloakId = JWTParser.parse(tokenUser).getJWTClaimsSet().getSubject();
        User userOrganizer = testDataUtils.createUserWithKeycloakId(organizerKeycloakId);
        User user = testDataUtils.createUserWithKeycloakId(userKeycloakId);

        Role r = null;

        // when
        ResultActions resultActions =
                mockMvc.perform(patchJsonRequest(r, tokenOrganizer, user.getId().toString(), "roles"));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}