package com.hackathonorganizer.userwriteservice.user.model.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ScheduleEntryResponse (

    Long id,

    String username,

    Long teamId,

    Long hackathonId,

    String info,

    String entryColor,

    boolean isAvailable,

    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    LocalDateTime sessionStart,

    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    LocalDateTime sessionEnd
) {

}

