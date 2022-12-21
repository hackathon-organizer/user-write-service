package com.hackathonorganizer.userwriteservice.user.model.dto;

import com.hackathonorganizer.userwriteservice.user.model.Tag;
import com.hackathonorganizer.userwriteservice.user.model.AccountType;

import java.util.List;
import java.util.Set;

public record UserResponseDto(

        Long id,

        String username,

        String description,
        String keyCloakId,
        Set<Tag> tags
) {
}
