package com.hackathonorganizer.userwriteservice.user.service;

import com.hackathonorganizer.userwriteservice.tag.dto.TagEditDto;
import com.hackathonorganizer.userwriteservice.tag.model.Tag;
import com.hackathonorganizer.userwriteservice.user.dto.EditUserRequestDto;
import com.hackathonorganizer.userwriteservice.user.dto.ScheduleMeetingRequest;
import com.hackathonorganizer.userwriteservice.user.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.user.dto.UserResponseDto;
import com.hackathonorganizer.userwriteservice.user.exception.UserNotFoundException;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.repository.ScheduleEntryRepository;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import com.hackathonorganizer.userwriteservice.utils.RestCommunicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public UserResponseDto createUser(String keyCloakId, String username) {
        User user = buildUser(keyCloakId, username);
        User savedUser = saveToRepository(user);
        return mapUserToResponseDto(savedUser);
    }

    private User saveToRepository(User user) {
        return userRepository.save(user);
    }

    private User buildUser(String keyCloakId, String username) {
        return User.builder()
                .username(username)
                .keyCloakId(keyCloakId)
                .build();
    }

    public UserResponseDto editUser(EditUserRequestDto editUserDto) {
        Long id = editUserDto.id();
        if (notExistsById(id)) {
            throw new UserNotFoundException(id);
        }
        User updatedUser = updateUserAndReturn(id, editUserDto);
        User savedUser = saveToRepository(updatedUser);
        return mapUserToResponseDto(savedUser);
    }

    private UserResponseDto mapUserToResponseDto(User savedUser) {
        return new UserResponseDto(savedUser);
    }

    private User updateUserAndReturn(Long id, EditUserRequestDto editUserDto) {
        User user = getUserById(id);
        user.setGithubProfileUrl(editUserDto.githubProfileUrl());
        user.setProfilePictureUrl(editUserDto.profilePictureUrl());
        user.setTags(mapTagsFromDto(editUserDto.tags()));
        return user;
    }

    private List<Tag> mapTagsFromDto(List<TagEditDto> tags) {
        return tags.stream()
                .map(tag -> mapTagFromDto(tag))
                .collect(Collectors.toList());
    }

    private Tag mapTagFromDto(TagEditDto tagDto) {
        return Tag.builder()
                .id(tagDto.id())
                .name(tagDto.name())
                .build();
    }

    public UserResponseDto blockUser(Long id) {
        User user = getUserById(id);
        user.setBlocked(true);
        keyCloakService.blockInKeycloak(user);
        User savedUser = saveToRepository(user);
        return mapUserToResponseDto(savedUser);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private boolean notExistsById(Long id) {
        return !userRepository.existsById(id);
    }

    public UserResponseDto updateUserHackathonMemership(Long userId,
            UserMembershipRequest userMembershipRequest) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        user.setCurrentHackathonId(userMembershipRequest.currentHackathonId());
        user.setCurrentTeamId(userMembershipRequest.currentTeamId());

        userRepository.save(user);

        return mapUserToResponseDto(user);
    }


    public void createUserScheduleEntry(Long userId,
            Set<ScheduleEntry> scheduleEntries) {

        User user =
                userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        scheduleEntries.forEach(scheduleEntry -> scheduleEntry.setUser(user));

        user.setScheduleEntries(scheduleEntries);
        userRepository.save(user);

        log.info("User with id: {} schedule updated successfully",
                user.getId());
    }

    public boolean updateUserHackathonSchedule(ScheduleMeetingRequest meetingRequest) {

        ScheduleEntry scheduleEntry =
                scheduleEntryRepository.findById(meetingRequest.entryId()).orElseThrow();

        boolean isOwner = restCommunicator.checkIfUserIsTeamOwner(meetingRequest.teamOwnerId(),
                meetingRequest.teamId());

        if (scheduleEntry.getTeamId() == null && isOwner) {
            scheduleEntry.setTeamId(meetingRequest.teamId());
            scheduleEntry.setAvailable(false);

            log.info("Team with id: {} assigned to user schedule successfully"
                    , meetingRequest.teamId());
        } else if (isOwner) {
            scheduleEntry.setTeamId(null);
            scheduleEntry.setAvailable(true);

            log.info("Team with id: {} unassigned from user schedule " +
                            "successfully"
                    , meetingRequest.teamId());
        } else {
            log.info("Only team owners can assign team for meeting");
        }

        ScheduleEntry savedEntry = scheduleEntryRepository.save(scheduleEntry);

        log.info("Schedule entry updated successfully");

        return savedEntry.isAvailable();
    }
}
