package com.hackathonorganizer.userwriteservice.user.model.dto;

public record UserMembershipRequest(

        Long id,
        Long currentHackathonId,
        Long currentTeamId
) {

}
