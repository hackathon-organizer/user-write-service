package com.hackathonorganizer.userwriteservice.user.controller;

import com.hackathonorganizer.userwriteservice.user.dto.EditUserRequestDto;
import com.hackathonorganizer.userwriteservice.user.dto.ScheduleMeetingRequest;
import com.hackathonorganizer.userwriteservice.user.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.user.dto.UserResponseDto;
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

    @PatchMapping
    UserResponseDto editUser(@Valid @RequestBody EditUserRequestDto editUserDto) {
        return userService.editUser(editUserDto);
    }

    @PutMapping("/{userId}/membership")
    UserResponseDto updateUserMembership(@PathVariable("userId") Long userId,
     @RequestBody UserMembershipRequest userMembershipRequest) {

        return userService.updateUserHackathonMemership(userId, userMembershipRequest);
    }

    @PatchMapping("/{userId}/block")
    UserResponseDto blockUser(@PathVariable("userId") Long userId) {
        return userService.blockUser(userId);
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
