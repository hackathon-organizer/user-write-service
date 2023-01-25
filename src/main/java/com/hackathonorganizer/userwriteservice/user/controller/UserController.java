package com.hackathonorganizer.userwriteservice.user.controller;

import com.hackathonorganizer.userwriteservice.user.keycloak.Role;
import com.hackathonorganizer.userwriteservice.user.model.dto.*;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/write/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    @RolesAllowed({"USER"})
    void editUser(@PathVariable("userId") Long userId, @RequestBody EditUserRequestDto editUserDto) {
        userService.editUser(userId, editUserDto);
    }

    @PatchMapping("/{userId}/membership")
    @RolesAllowed({"USER"})
    void updateUserMembership(@PathVariable("userId") Long userId, @RequestBody UserMembershipRequest userMembershipRequest,
                              Principal principal) {
        userService.updateUserHackathonMembership(userId, userMembershipRequest, principal);
    }

    @PatchMapping("/{userId}/block")
    @RolesAllowed({"ORGANIZER"})
    void blockUser(@PathVariable("userId") Long userId) {
        userService.blockUser(userId);
    }

    @PostMapping("/{userId}/schedule")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    ScheduleEntryResponse createUserScheduleEntry(@PathVariable("userId") Long userId, @RequestBody ScheduleEntryRequest scheduleEntryRequest,
                                                  Principal principal) {
        return userService.createUserScheduleEntry(userId, scheduleEntryRequest, principal);
    }

    @PutMapping("/{userId}/schedule")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    void updateUserScheduleEntries(@RequestBody List<ScheduleEntryRequest> scheduleEntries, Principal principal) {
        userService.updateUserScheduleEntries(scheduleEntries, principal);
    }

    @PatchMapping("/{userId}/schedule/{entryId}")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    void updateUserScheduleEntryTime(@PathVariable("entryId") Long entryId, @RequestBody ScheduleEntryRequest scheduleEntry,
                                  Principal principal) {

        userService.updateUserScheduleEntryTime(entryId, scheduleEntry, principal);
    }

    @DeleteMapping("/{userId}/schedule/{entryId}")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    void deleteScheduleEntry(@PathVariable("userId") Long userId, @PathVariable("entryId") Long entryId, Principal principal) {
        userService.deleteScheduleEntry(userId, entryId, principal);
    }

    @PatchMapping("/schedule/{entryId}/meeting")
    @RolesAllowed({"TEAM_OWNER"})
    boolean assignTeamToMeetingWithMentor(@PathVariable("entryId") Long entryId, @RequestBody ScheduleMeetingRequest scheduleMeetingRequest,
                                          Principal principal) {
        return userService.updateScheduleEntryAvailabilityStatus(entryId, scheduleMeetingRequest, principal);
    }

    @PatchMapping("/{userId}/roles")
    @RolesAllowed("ORGANIZER")
    void updateUserRole(@PathVariable("userId") Long userId, @RequestBody Role role) {
        userService.updateUserRole(userId, role);
    }
}
