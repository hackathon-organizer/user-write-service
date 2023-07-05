package com.hackathonorganizer.userwriteservice.user.model.dto;

public record ScheduleMeetingRequest(
        Long teamId,
        Long teamOwnerId
) {

}
