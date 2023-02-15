package com.hackathonorganizer.userwriteservice.utils;

import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.dto.ScheduleEntryResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScheduleMapper {

    public static ScheduleEntryResponse mapToDto(ScheduleEntry scheduleEntry, String username, Long userId) {

        return new ScheduleEntryResponse(
                scheduleEntry.getId(),
                username,
                scheduleEntry.getTeamId(),
                userId,
                scheduleEntry.getHackathonId(),
                scheduleEntry.getInfo(),
                scheduleEntry.getEntryColor(),
                scheduleEntry.isAvailable(),
                scheduleEntry.getSessionStart(),
                scheduleEntry.getSessionEnd()
        );
    }
}
