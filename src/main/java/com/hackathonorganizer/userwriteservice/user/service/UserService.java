package com.hackathonorganizer.userwriteservice.user.service;

import com.hackathonorganizer.userwriteservice.exception.ScheduleException;
import com.hackathonorganizer.userwriteservice.exception.UserException;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.keycloak.Role;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.model.dto.*;
import com.hackathonorganizer.userwriteservice.user.repository.ScheduleEntryRepository;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import com.hackathonorganizer.userwriteservice.utils.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keyCloakService;
    private final ScheduleEntryRepository scheduleEntryRepository;

    public void createUser(String keyCloakId, String username) {

        User user = User.builder()
                .username(username)
                .keyCloakId(keyCloakId)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User with username: {} saved successfully", savedUser.getUsername());
    }

    public void updateUser(Long userId, UserUpdateRequest userUpdateRequest, Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            user.setDescription(userUpdateRequest.description());
            user.setTags(userUpdateRequest.tags());

            userRepository.save(user);

            log.info("User with id: {} updated successfully", userId);
        }
    }

    public void blockUser(Long userId, Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            user.setBlocked(true);
            keyCloakService.blockInKeycloak(user);

            userRepository.save(user);

            log.info("User with id: {} account blocked", userId);
        }
    }

    public void updateUserHackathonMembership(Long userId,
                                              UserMembershipRequest userMembershipRequest,
                                              Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            user.setCurrentHackathonId(userMembershipRequest.currentHackathonId());
            user.setCurrentTeamId(userMembershipRequest.currentTeamId());

            userRepository.save(user);

            log.info("User with id: {} membership updated successfully", userId);
        }
    }

    public ScheduleEntryResponse createUserScheduleEntry(Long userId,
                                                         ScheduleEntryRequest scheduleEntryRequest,
                                                         Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId()) &&
                isDateValid(scheduleEntryRequest.sessionStart(), scheduleEntryRequest.sessionEnd())) {

            ScheduleEntry scheduleEntry = ScheduleEntry.builder()
                    .hackathonId(scheduleEntryRequest.hackathonId())
                    .info(scheduleEntryRequest.info())
                    .entryColor(scheduleEntryRequest.entryColor())
                    .sessionStart(scheduleEntryRequest.sessionStart())
                    .sessionEnd(scheduleEntryRequest.sessionEnd())
                    .user(user)
                    .build();

            ScheduleEntry savedEntry = scheduleEntryRepository.save(scheduleEntry);

            log.info("User with id: {} schedule entry created successfully", user.getId());

            return ScheduleMapper.mapToDto(savedEntry, user.getUsername(), user.getId());
        } else {
            log.info("Can't create new schedule entry for user with id: {}", user.getId());

            throw new UserException("Can't create new schedule entry for user with id: " + user.getId(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateUserScheduleEntries(Long userId,
                                          List<ScheduleEntryRequest> scheduleEntries,
                                          Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            scheduleEntries.forEach(entry -> {

                if (isDateValid(entry.sessionStart(), entry.sessionEnd())) {

                    ScheduleEntry entryToUpdate = getScheduleEntryById(entry.id());

                    entryToUpdate.setSessionStart(entry.sessionStart());
                    entryToUpdate.setSessionEnd(entry.sessionEnd());
                    entryToUpdate.setEntryColor(entry.entryColor());
                    entryToUpdate.setInfo(entry.info());

                    scheduleEntryRepository.save(entryToUpdate);
                }
            });

            log.info("Schedule for user with id: {} updated successfully", user.getId());
        }
    }

    public boolean updateScheduleEntryAvailabilityStatus(Long entryId,
                                                         ScheduleMeetingRequest meetingRequest,
                                                         Principal principal) {

        User user = getUserByKeycloakId(principal);

        ScheduleEntry scheduleEntry = getScheduleEntryById(entryId);

        if (!scheduleEntry.isAvailable() && user.getCurrentTeamId().equals(scheduleEntry.getTeamId())) {

            scheduleEntry.setTeamId(null);
            scheduleEntry.setAvailable(true);

            log.info("Team with id: {} assigned to user schedule successfully", meetingRequest.teamId());
        } else if (scheduleEntry.isAvailable()) {

            scheduleEntry.setTeamId(meetingRequest.teamId());
            scheduleEntry.setAvailable(false);

            log.info("Team with id: {} unassigned from user schedule successfully", meetingRequest.teamId());
        } else {
            log.info("User is not owner of team with id: {}", scheduleEntry.getTeamId());
            throw new ScheduleException("User is not owner of team with id: " + scheduleEntry.getTeamId(),
                    HttpStatus.FORBIDDEN);
        }

        ScheduleEntry savedEntry = scheduleEntryRepository.save(scheduleEntry);

        log.info("Schedule entry updated successfully");

        return savedEntry.isAvailable();
    }

    public void updateUserScheduleEntryTime(Long entryId,
                                            ScheduleEntryRequest scheduleEntryRequest,
                                            Principal principal) {

        ScheduleEntry scheduleEntry = getScheduleEntryById(entryId);

        if (verifyUser(principal, scheduleEntry.getUser().getKeyCloakId()) &&
                isDateValid(scheduleEntryRequest.sessionStart(), scheduleEntryRequest.sessionEnd())) {
            scheduleEntry.setSessionStart(scheduleEntryRequest.sessionStart());
            scheduleEntry.setSessionEnd(scheduleEntryRequest.sessionEnd());

            scheduleEntryRepository.save(scheduleEntry);

            log.info("Schedule entry with id: {} event time updated successfully", entryId);
        }
    }

    public void deleteScheduleEntry(Long entryId, Principal principal) {

        ScheduleEntry scheduleEntry = getScheduleEntryById(entryId);

        if (verifyUser(principal, scheduleEntry.getUser().getKeyCloakId())) {

            scheduleEntryRepository.deleteById(entryId);

            log.info("Schedule entry with id: {} deleted successfully", entryId);
        }
    }

    private boolean verifyUser(Principal principal, String userKeycloakId) {

        if (principal.getName().equals(userKeycloakId)) {
            return true;
        } else {
            log.info("User verification failed ids: {} and {}", principal.getName(), userKeycloakId);

            throw new UserException("User verification failed", HttpStatus.FORBIDDEN);
        }
    }

    public void updateUserRole(Long userId, Role role, Principal principal) {

        User organizer = getUserByKeycloakId(principal);
        User user = getUserById(userId);

        if (organizer.getCurrentHackathonId().equals(user.getCurrentHackathonId())) {
            keyCloakService.updateUserRole(user.getKeyCloakId(), role);
        } else {
            log.info("User {} and Organizer {} are not participants of the same hackathon", userId, organizer.getId());

            throw new UserException(String.format("User %d and Organizer %d are not participants of the same hackathon",
                    userId, organizer.getId()), HttpStatus.FORBIDDEN);
        }
    }

    private User getUserById(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new UserException(String.format("User with id: %d not found", userId), HttpStatus.NOT_FOUND));
    }

    private User getUserByKeycloakId(Principal principal) {

        return userRepository.findByKeyCloakId(principal.getName()).orElseThrow(
                () -> new UserException(String.format("User with id: %s not found", principal.getName()),
                        HttpStatus.NOT_FOUND));
    }

    private ScheduleEntry getScheduleEntryById(Long entryId) {

        return scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ScheduleException(String.format("Schedule entry with id: %d not found", entryId),
                        HttpStatus.NOT_FOUND));
    }

    private boolean isDateValid(OffsetDateTime start, OffsetDateTime end) {

        if (start.isAfter(OffsetDateTime.now()) && start.isBefore(end)) {
            return true;
        } else {
            throw new UserException("Event dates are invalid", HttpStatus.BAD_REQUEST);
        }
    }
}
