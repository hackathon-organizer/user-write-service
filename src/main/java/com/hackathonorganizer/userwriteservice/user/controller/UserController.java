package com.hackathonorganizer.userwriteservice.user.controller;

import com.hackathonorganizer.userwriteservice.user.dto.EditUserRequestDto;
import com.hackathonorganizer.userwriteservice.user.dto.UserMembershipRequest;
import com.hackathonorganizer.userwriteservice.user.dto.UserResponseDto;
import com.hackathonorganizer.userwriteservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

        return userService.updateUserHackathonMembership(userId, userMembershipRequest);
    }

    @PatchMapping("/{id}/block")
    UserResponseDto blockUser(@PathVariable("id") Long id) {
        return userService.blockUser(id);
    }
}
