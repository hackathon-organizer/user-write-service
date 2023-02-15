package com.hackathonorganizer.userwriteservice.user.model.dto;

import com.hackathonorganizer.userwriteservice.user.model.Tag;

import java.util.Set;

public record UserEditDto(

        String description,
        Set<Tag> tags
) {

}
