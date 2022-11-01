package com.hackathonorganizer.userwriteservice.user.service;

import com.hackathonorganizer.userwriteservice.tag.dto.TagEditDto;
import com.hackathonorganizer.userwriteservice.tag.model.Tag;
import com.hackathonorganizer.userwriteservice.user.dto.EditUserRequestDto;
import com.hackathonorganizer.userwriteservice.user.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.user.dto.UserResponseDto;
import com.hackathonorganizer.userwriteservice.user.exception.UserNotFoundException;
import com.hackathonorganizer.userwriteservice.user.keycloak.KeycloakService;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keyCloakService;

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
                .keycloakId(keyCloakId)
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

    public UserResponseDto updateUserHackathonMembership(Long userId,
                                                         UserMembershipRequest userMembershipRequest) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        user.setCurrentHackathonId(userMembershipRequest.currentHackathonId());
        user.setCurrentTeamId(userMembershipRequest.currentTeamId());

        userRepository.save(user);

        return mapUserToResponseDto(user);
    }
}
