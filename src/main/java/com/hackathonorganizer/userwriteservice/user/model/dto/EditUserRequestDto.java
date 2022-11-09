package com.hackathonorganizer.userwriteservice.user.model.dto;

import com.hackathonorganizer.userwriteservice.user.model.Tag;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public record EditUserRequestDto(
        @NotEmpty
        String githubProfileUrl,

        @NotEmpty
        String profilePictureUrl,

        @NotNull
        List<Tag> tags
) {

}
