package com.hackathonorganizer.userwriteservice.user.dto;

import com.hackathonorganizer.userwriteservice.tag.dto.TagResponseDto;
import com.hackathonorganizer.userwriteservice.tag.model.Tag;
import com.hackathonorganizer.userwriteservice.user.model.AccountType;
import com.hackathonorganizer.userwriteservice.user.model.User;

import java.util.List;

public record UserResponseDto(

        Long id,

        String username,

        String keyCloakId,

        AccountType accountType,

        String githubProfileUrl,

        String profilePictureUrl,

        boolean blocked,

        List<TagResponseDto> tags
) {

    public UserResponseDto(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getKeycloakId(),
                user.getAccountType(),
                user.getGithubProfileUrl(),
                user.getProfilePictureUrl(),
                user.isBlocked(),
                mapTagsToDto(user.getTags())
        );
    }

    private static List<TagResponseDto> mapTagsToDto(List<Tag> tags) {
        return tags.stream()
                .map(tag -> new TagResponseDto(tag))
                .toList();
    }
}
