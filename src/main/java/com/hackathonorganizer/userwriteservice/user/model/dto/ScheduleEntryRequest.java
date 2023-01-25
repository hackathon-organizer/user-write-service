package com.hackathonorganizer.userwriteservice.user.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public record ScheduleEntryRequest(

        @NotNull
        Long id,

        @NotNull
        Long hackathonId,

        String info,

        String entryColor,

        @NotNull
        @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
        OffsetDateTime sessionStart,

        @NotNull
        @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
        OffsetDateTime sessionEnd
) {
}
