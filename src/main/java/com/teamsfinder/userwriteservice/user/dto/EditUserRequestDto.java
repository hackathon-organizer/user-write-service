package com.teamsfinder.userwriteservice.user.dto;

import com.teamsfinder.userwriteservice.tag.dto.TagEditDto;
import com.teamsfinder.userwriteservice.user.model.TeamInvitation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public record EditUserRequestDto(

        @NotNull
        Long id,

        @NotEmpty
        String githubProfileUrl,

        @NotEmpty
        String profilePictureUrl,

        Set<TeamInvitation> invitations,

        @NotNull
        List<TagEditDto> tags
) {

}
