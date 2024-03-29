package com.hackathonorganizer.userwriteservice.user.controller;

import com.hackathonorganizer.userwriteservice.user.keycloak.Role;
import com.hackathonorganizer.userwriteservice.user.model.dto.*;
import com.hackathonorganizer.userwriteservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/v1/write/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    @RolesAllowed({"USER"})
    public void editUser(@PathVariable("userId") Long userId,
                         @RequestBody UserUpdateRequest userUpdateRequest,
                         Principal principal) {
        userService.updateUser(userId, userUpdateRequest, principal);
    }

    @PatchMapping("/{userId}/membership")
    @RolesAllowed({"USER"})
    public void updateUserMembership(@PathVariable("userId") Long userId,
                                     @RequestBody UserMembershipRequest userMembershipRequest,
                                     Principal principal) {
        userService.updateUserHackathonMembership(userId, userMembershipRequest, principal);
    }

    @PatchMapping("/{userId}/block")
    @RolesAllowed({"ORGANIZER"})
    public void blockUser(@PathVariable("userId") Long userId, Principal principal) {
        userService.blockUser(userId, principal);
    }

    @PostMapping("/{userId}/schedule")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleEntryResponse createUserScheduleEntry(@PathVariable("userId") Long userId,
                                                         @RequestBody @Valid ScheduleEntryRequest scheduleEntryRequest,
                                                         Principal principal) {
        return userService.createUserScheduleEntry(userId, scheduleEntryRequest, principal);
    }

    @PutMapping("/{userId}/schedule")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    public void updateUserScheduleEntries(@PathVariable("userId") Long userId,
                                          @RequestBody @Valid List<ScheduleEntryRequest> scheduleEntries,
                                          Principal principal) {
        userService.updateUserScheduleEntries(userId, scheduleEntries, principal);
    }

    @PatchMapping("/{userId}/schedule/{entryId}")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    public void updateUserScheduleEntryTime(@PathVariable("entryId") Long entryId,
                                            @RequestBody @Valid ScheduleEntryRequest scheduleEntry,
                                            Principal principal) {
        userService.updateUserScheduleEntryTime(entryId, scheduleEntry, principal);
    }

    @DeleteMapping("/{userId}/schedule/{entryId}")
    @RolesAllowed({"MENTOR", "ORGANIZER"})
    public void deleteScheduleEntry(@PathVariable("entryId") Long entryId,
                                    Principal principal) {
        userService.deleteScheduleEntry(entryId, principal);
    }

    @PatchMapping("/schedule/{entryId}/meeting")
    @RolesAllowed({"TEAM_OWNER"})
    public boolean assignTeamToMeetingWithMentor(@PathVariable("entryId") Long entryId,
                                                 @RequestBody ScheduleMeetingRequest scheduleMeetingRequest,
                                                 Principal principal) {
        return userService.updateScheduleEntryAvailabilityStatus(entryId, scheduleMeetingRequest, principal);
    }

    @PatchMapping("/{userId}/roles")
    @RolesAllowed("ORGANIZER")
    public void updateUserRole(@PathVariable("userId") Long userId, @RequestBody Role role, Principal principal) {
        userService.updateUserRole(userId, role, principal);
    }
}
