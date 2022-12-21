package com.hackathonorganizer.userwriteservice.user.service;

import com.hackathonorganizer.userwriteservice.user.model.dto.*;
import com.hackathonorganizer.userwriteservice.exception.ScheduleException;
import com.hackathonorganizer.userwriteservice.exception.UserException;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.repository.ScheduleEntryRepository;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
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

        userRepository.save(user);

        log.info("User with username: {} created successfully", username);
    }

    public void editUser(Long userId, EditUserRequestDto editUserDto) {

        User user = getUserById(userId);

        user.setDescription(editUserDto.description());
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

    public void updateUserHackathonMembership(Long userId, UserMembershipRequest userMembershipRequest, Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            user.setCurrentHackathonId(userMembershipRequest.currentHackathonId());
            user.setCurrentTeamId(userMembershipRequest.currentTeamId());

            userRepository.save(user);

            log.info("User with id: {} membership updated successfully", userId);
        } else {
            log.info("Can't update user {} membership", user.getId());

            throw new UserException("Can't update user: " + user.getId() + " membership",
                    HttpStatus.FORBIDDEN);
        }
    }

    public ScheduleEntryResponse createUserScheduleEntry(Long userId, ScheduleEntryRequest scheduleEntryRequest,
                                                         Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            ScheduleEntry scheduleEntry = ScheduleEntry.builder()
                    .hackathonId(scheduleEntryRequest.hackathonId())
                    .info(scheduleEntryRequest.info())
                    .entryColor(scheduleEntryRequest.entryColor())
                    .sessionStart(scheduleEntryRequest.sessionStart())
                    .sessionEnd(scheduleEntryRequest.sessionEnd())
                    .user(user)
                    .build();

            ScheduleEntry savedEntry = scheduleEntryRepository.save(scheduleEntry);

            user.addScheduleEntry(savedEntry);
            userRepository.save(user);

            log.info("User with id: {} schedule created successfully", user.getId());

            return new ScheduleEntryResponse(
                    savedEntry.getId(),
                    user.getUsername(),
                    savedEntry.getTeamId(),
                    savedEntry.getHackathonId(),
                    savedEntry.getInfo(),
                    savedEntry.getEntryColor(),
                    savedEntry.isAvailable(),
                    savedEntry.getSessionStart(),
                    savedEntry.getSessionEnd()
            );
        } else {
            log.info("Can't create new schedule entry for user with id: {}", user.getId());

            throw new UserException("Can't create new schedule entry for user with id: " + user.getId(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateUserScheduleEntries(Long userId, List<ScheduleEntryRequest> scheduleEntries, Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId())) {

            scheduleEntries.forEach(entry -> {

                ScheduleEntry entryToUpdate = getScheduleEntryById(entry.id());

                entryToUpdate.setSessionStart(entry.sessionStart());
                entryToUpdate.setSessionEnd(entry.sessionEnd());
                entryToUpdate.setEntryColor(entry.entryColor());
                entryToUpdate.setInfo(entry.info());

                scheduleEntryRepository.save(entryToUpdate);
            });

            log.info("User with id: {} schedule updated successfully", user.getId());
        }  else {
            log.info("Can't update user {} schedule", user.getId());

            throw new UserException("Can't update user: " + user.getId() + " schedule",
                    HttpStatus.FORBIDDEN);
        }
    }

    public boolean updateScheduleEntryAvailabilityStatus(Long entryId, ScheduleMeetingRequest meetingRequest, Principal principal) {

        User user = getUserById(meetingRequest.teamOwnerId());
        ScheduleEntry scheduleEntry = getScheduleEntryById(entryId);

        if (verifyUser(principal, user.getKeyCloakId()) &&
                user.getCurrentTeamId().equals(meetingRequest.teamId())) {

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

            throw new UserException("Only team owners can assign team for meeting", HttpStatus.FORBIDDEN);
        }
    }

    public void updateUserScheduleEntryTime(Long userId, Long entryId, ScheduleEntryRequest scheduleEntryRequest,
                                            Principal principal) {

        User user = getUserById(userId);

        ScheduleEntry scheduleEntry = getScheduleEntryById(entryId);

        if (verifyUser(principal, user.getKeyCloakId())
                && containScheduleEntry(user, scheduleEntryRequest.id())) {

            scheduleEntry.setSessionStart(scheduleEntryRequest.sessionStart());
            scheduleEntry.setSessionEnd(scheduleEntryRequest.sessionEnd());

            scheduleEntryRepository.save(scheduleEntry);

            log.info("Schedule entry with id: {} event time updated successfully", entryId);
        }
    }

    public void deleteScheduleEntry(Long userId, Long entryId, Principal principal) {

        User user = getUserById(userId);

        if (verifyUser(principal, user.getKeyCloakId()) && containScheduleEntry(user, entryId)) {

            scheduleEntryRepository.deleteById(entryId);

            log.info("Schedule entry with id: {} deleted successfully", entryId);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserException(String.format("User with id: %d not found", userId), HttpStatus.NOT_FOUND));
    }

    private ScheduleEntry getScheduleEntryById(Long entryId) {
        return scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ScheduleException(String.format("Schedule entry with id: %d not found", entryId),
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

    private boolean containScheduleEntry(User user, Long entryId) {
        return user.getScheduleEntries().stream().anyMatch(entry -> entry.getId().equals(entryId));
    }
}
