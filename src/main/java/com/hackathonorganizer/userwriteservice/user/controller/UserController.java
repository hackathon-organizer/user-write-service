package com.hackathonorganizer.userwriteservice.user.controller;

import com.hackathonorganizer.userwriteservice.user.model.dto.EditUserRequestDto;
import com.hackathonorganizer.userwriteservice.user.model.dto.ScheduleMeetingRequest;
import com.hackathonorganizer.userwriteservice.user.model.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntryRequest;
import com.hackathonorganizer.userwriteservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/write/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    void editUser(@PathVariable("userId") Long userId,
            @Valid @RequestBody EditUserRequestDto editUserDto) {

        userService.editUser(userId, editUserDto);
    }

    @PatchMapping("/{userId}/membership")
    void updateUserMembership(@PathVariable("userId") Long userId,
     @RequestBody UserMembershipRequest userMembershipRequest) {

        userService.updateUserHackathonMembership(userId, userMembershipRequest);
    }

    @PatchMapping("/{userId}/block")
    void blockUser(@PathVariable("userId") Long userId) {
        userService.blockUser(userId);
    }

    @PostMapping("/{userId}/schedule")
    void createUserScheduleEntry(@PathVariable("userId") Long userId,
            @RequestBody Set<ScheduleEntry> scheduleEntries) {

        userService.updateUserScheduleEntry(userId, scheduleEntries);
    }

    @PatchMapping("/schedule")
    boolean updateUserSchedule(@RequestBody ScheduleMeetingRequest meetingRequest) {

        return userService.updateUserHackathonSchedule(meetingRequest);
    }

    @DeleteMapping("/schedule/{entryId}")
    void deleteScheduleEntry(@PathVariable("entryId") Long entryId) {

        userService.deleteScheduleEntry(entryId);
    }

    @PatchMapping("/schedule/{entryId}")
    void updateUserScheduleEntry(@PathVariable("entryId") Long entryId,
            @RequestBody ScheduleEntryRequest scheduleEntry) {

        userService.updateUserScheduleEntryTime(entryId, scheduleEntry);
    }

}
