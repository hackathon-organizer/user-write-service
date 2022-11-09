package com.hackathonorganizer.userwriteservice.user.dto;

import com.hackathonorganizer.userwriteservice.tag.model.Tag;
import com.hackathonorganizer.userwriteservice.user.model.AccountType;

import java.util.List;

public record UserResponseDto(

        Long id,

        String username,

        String keyCloakId,

        AccountType accountType,

        String githubProfileUrl,

        String profilePictureUrl,

        boolean blocked,

        List<Tag> tags
) {
}
