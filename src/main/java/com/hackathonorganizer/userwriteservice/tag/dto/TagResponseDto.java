package com.hackathonorganizer.userwriteservice.tag.dto;

import com.hackathonorganizer.userwriteservice.tag.model.Tag;

public record TagResponseDto(

        Long id,

        String name
) {

    public TagResponseDto(Tag tag) {
        this(tag.getId(), tag.getName());
    }
}
