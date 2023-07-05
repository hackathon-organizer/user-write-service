package com.hackathonorganizer.userwriteservice.user.model.dto;

import com.hackathonorganizer.userwriteservice.user.model.Tag;

import java.util.Set;

public record UserUpdateRequest(

        String description,
        Set<Tag> tags
) {

}
