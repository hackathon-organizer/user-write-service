package com.hackathonorganizer.userwriteservice.user.model.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ScheduleEntryRequest(

        Long id,

        @NotNull(message = "Hackathon id can not be null!")
        Long hackathonId,

        String info,

        String entryColor,

        @NotNull(message = "Event start date can not be null!")
        OffsetDateTime sessionStart,

        @NotNull(message = "Event end date can not be null!")
        OffsetDateTime sessionEnd
) {
}
