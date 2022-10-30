package com.hackathonorganizer.userwriteservice.user.model;

import java.time.LocalDateTime;

public record ScheduleEntryRequest(
        LocalDateTime sessionStart,
        LocalDateTime sessionEnd
) {

}
