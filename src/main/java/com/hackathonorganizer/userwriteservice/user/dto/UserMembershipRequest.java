package com.hackathonorganizer.userwriteservice.user.dto;

public record UserMembershipRequest(

        Long id,
        Long currentHackathonId,
        Long currentTeamId
) {

}
