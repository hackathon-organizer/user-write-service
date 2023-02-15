package com.hackathonorganizer.userwriteservice.user.model.dto;

import com.hackathonorganizer.userwriteservice.user.model.Tag;

import java.util.Set;

public record UserDto(

        Long id,
        String username,
        String description,
        String keyCloakId,
        Set<Tag> tags
) {
}
