package com.hackathonorganizer.userwriteservice.user.model.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public record ScheduleEntryResponse (

    Long id,

    String username,

    Long teamId,

    Long hackathonId,

    String info,

    String entryColor,

    boolean isAvailable,

    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    OffsetDateTime sessionStart,

    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    OffsetDateTime sessionEnd
) {

}

