package com.hackathonorganizer.userwriteservice.user.controller;

import com.hackathonorganizer.userwriteservice.user.model.dto.EditUserRequestDto;
import com.hackathonorganizer.userwriteservice.user.model.dto.ScheduleMeetingRequest;
import com.hackathonorganizer.userwriteservice.user.model.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntryRequest;
import com.hackathonorganizer.userwriteservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/write/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    @RolesAllowed({"USER"})
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
    @RolesAllowed({"ORGANIZER"})
    void blockUser(@PathVariable("userId") Long userId) {

        userService.blockUser(userId);
    }

    @PostMapping("/{userId}/schedule")
    @RolesAllowed({"MENTOR","ORGANIZER"})
    void createUserScheduleEntry(Principal principal, @PathVariable("userId") Long userId,
            @RequestBody ScheduleEntry scheduleEntry) {

        userService.createUserScheduleEntry(principal, userId, scheduleEntry);
    }

    @PutMapping("/{userId}/schedule")
    @RolesAllowed({"MENTOR","ORGANIZER"})
    void updateUserScheduleEntries(Principal principal, @PathVariable("userId") Long userId,
            @RequestBody List<ScheduleEntry> scheduleEntries) {

        userService.updateUserScheduleEntries(principal, userId, scheduleEntries);
    }

    @DeleteMapping("/{userId}/schedule/{entryId}")
    @RolesAllowed({"MENTOR","ORGANIZER"})
    void deleteScheduleEntry(@PathVariable("userId") Long userId,
            @PathVariable("entryId") Long entryId) {

        userService.deleteScheduleEntry(userId, entryId);
    }

    @PatchMapping("/{userId}/schedule/{entryId}")
    @RolesAllowed({"MENTOR","ORGANIZER"})
    void updateUserScheduleEntry(
            Principal principal,
            @PathVariable("userId") Long userId,
            @PathVariable("entryId") Long entryId,
            @RequestBody ScheduleEntryRequest scheduleEntry) {

        userService.updateUserScheduleEntryTime(principal, userId, entryId, scheduleEntry);
    }

    @PatchMapping("/schedule/{entryId}/meeting")
    @RolesAllowed({"TEAM_OWNER","ORGANIZER"})
    boolean assignTeamToMeetingWithMentor(
            Principal principal,
            @PathVariable("entryId") Long entryId,
            @RequestBody ScheduleMeetingRequest scheduleMeetingRequest) {

        return userService.updateScheduleEntryAvailabilityStatus(principal, entryId,
                scheduleMeetingRequest);
    }

}
