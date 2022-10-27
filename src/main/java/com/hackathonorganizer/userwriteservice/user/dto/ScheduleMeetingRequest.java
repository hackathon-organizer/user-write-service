package com.hackathonorganizer.userwriteservice.user.dto;

public record ScheduleMeetingRequest(
        Long teamId,
        Long teamOwnerId,
        Long entryId
) {

}
