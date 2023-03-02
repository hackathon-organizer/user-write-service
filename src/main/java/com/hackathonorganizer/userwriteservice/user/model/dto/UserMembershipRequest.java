package com.hackathonorganizer.userwriteservice.user.model.dto;

public record UserMembershipRequest(

        Long userId,
        Long currentHackathonId,
        Long currentTeamId
) {

}
