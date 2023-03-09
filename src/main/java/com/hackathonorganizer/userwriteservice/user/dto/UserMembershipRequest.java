package com.hackathonorganizer.userwriteservice.user.dto;

import javax.validation.constraints.NotNull;

public record UserMembershipRequest(

        @NotNull
        Long id,
        @NotNull
        Long currentHackathonId,
        @NotNull
        Long currentTeamId
) {

}
