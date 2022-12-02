package com.hackathonorganizer.userwriteservice.user.service;

import com.hackathonorganizer.userwriteservice.user.model.dto.EditUserRequestDto;
import com.hackathonorganizer.userwriteservice.user.model.dto.ScheduleMeetingRequest;
import com.hackathonorganizer.userwriteservice.user.model.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.exception.ScheduleException;
import com.hackathonorganizer.userwriteservice.exception.UserException;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntryRequest;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.repository.ScheduleEntryRepository;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import com.hackathonorganizer.userwriteservice.utils.RestCommunicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keyCloakService;
    private final ScheduleEntryRepository scheduleEntryRepository;
    private final RestCommunicator restCommunicator;

    public void createUser(String keyCloakId, String username) {

        User user = User.builder()
                .username(username)
                .keyCloakId(keyCloakId)
                .build();

        userRepository.save(user);

        log.info("User with username: {} created successfully", username);
    }

    public void editUser(Long userId, EditUserRequestDto editUserDto) {

        User user = getUserById(userId);

        user.setGithubProfileUrl(editUserDto.githubProfileUrl());
        user.setProfilePictureUrl(editUserDto.profilePictureUrl());
        user.setTags(editUserDto.tags());

        userRepository.save(user);

        log.info("User with id: {} updated successfully", userId);
    }

    public void blockUser(Long userId) {
        User user = getUserById(userId);
        user.setBlocked(true);
        keyCloakService.blockInKeycloak(user);

        userRepository.save(user);

        log.info("User with id: {} account blocked", userId);
    }

    public void updateUserHackathonMembership(Long userId, UserMembershipRequest userMembershipRequest) {

        User user = getUserById(userId);

        user.setCurrentHackathonId(userMembershipRequest.currentHackathonId());
        user.setCurrentTeamId(userMembershipRequest.currentTeamId());

        userRepository.save(user);

        log.info("User with id: {} membership updated successfully", userId);
    }

    public void createUserScheduleEntry(Principal principal, Long userId,
            ScheduleEntry scheduleEntry) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

        scheduleEntry.setUser(user);
        scheduleEntry.setHackathonId(user.getCurrentHackathonId());

        user.addScheduleEntry(scheduleEntry);
        userRepository.save(user);

        log.info("User with id: {} schedule created successfully", user.getId());
        }
    }

    public void updateUserScheduleEntries(Principal principal, Long userId,
            List<ScheduleEntry> scheduleEntries) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            scheduleEntries.forEach(entry -> {

                ScheduleEntry entryToUpdate = getScheduleEntryById(entry.getId());

                entryToUpdate.setSessionStart(entry.getSessionStart());
                entryToUpdate.setSessionEnd(entry.getSessionEnd());
                entryToUpdate.setEntryColor(entry.getEntryColor());
                entryToUpdate.setInfo(entry.getInfo());

                scheduleEntryRepository.save(entryToUpdate);
            });

            log.info("User with id: {} schedule updated successfully", user.getId());
        }
    }

    public boolean updateScheduleEntryAvailabilityStatus(Principal principal,
             Long entryId, ScheduleMeetingRequest meetingRequest) {

        User user = getUserById(meetingRequest.teamOwnerId());
        ScheduleEntry scheduleEntry = getScheduleEntryById(entryId);

        if (verifyUser(principal, user.getKeyCloakId()) &&
        user.getCurrentTeamId().equals(meetingRequest.teamId())) {

//        boolean isOwner = restCommunicator.checkIfUserIsTeamOwner(meetingRequest.teamOwnerId(),
//                meetingRequest.teamId());

        if (scheduleEntry.isAvailable()) {
            scheduleEntry.setTeamId(meetingRequest.teamId());
            scheduleEntry.setAvailable(false);

            log.info("Team with id: {} assigned to user schedule successfully"
                    , meetingRequest.teamId());
        } else {
            scheduleEntry.setTeamId(null);
            scheduleEntry.setAvailable(true);

            log.info("Team with id: {} unassigned from user schedule successfully",
                    meetingRequest.teamId());
        }

        ScheduleEntry savedEntry = scheduleEntryRepository.save(scheduleEntry);

        log.info("Schedule entry updated successfully");

        return savedEntry.isAvailable();

        } else {
            log.info("Only team owners can assign team for meeting");
            return false;
        }
    }

    public void updateUserScheduleEntryTime(Principal principal,
            Long userId, Long entryId, ScheduleEntryRequest scheduleEntryRequest) {

        User user = getUserById(userId);

        ScheduleEntry scheduleEntry = getScheduleEntryById(entryId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            scheduleEntry.setSessionStart(scheduleEntryRequest.sessionStart());
            scheduleEntry.setSessionEnd(scheduleEntryRequest.sessionEnd());

            scheduleEntryRepository.save(scheduleEntry);

            log.info("Schedule entry with id: {} event time updated successfully", entryId);
        }
    }

    public void deleteScheduleEntry(Long userId, Long entryId) {

        // TODO check if entry belongs to user

        scheduleEntryRepository.deleteById(entryId);

        log.info("Schedule entry with id: {} deleted successfully", entryId);
    }

    private User getUserById(Long userId) {
        return  userRepository.findById(userId).orElseThrow(
                () -> new UserException(String.format("User with id: %d not found", userId),
                        HttpStatus.NOT_FOUND));
    }

    private ScheduleEntry getScheduleEntryById(Long entryId) {
        return scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ScheduleException(
                        String.format("Schedule entry with id: %d not found", entryId),
                        HttpStatus.NOT_FOUND));
    }

    private boolean verifyUser(Principal principal, String userKeycloakId) {

        if (principal.getName().equals(userKeycloakId)) {
            return true;
        } else {
            log.warn("User verification failed id: {} to id: {}",
                    principal.getName(), userKeycloakId);

            throw new UserException("User verification failed", HttpStatus.FORBIDDEN);
        }
    }
}
