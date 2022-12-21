package com.hackathonorganizer.userwriteservice.utils;

import com.hackathonorganizer.userwriteservice.user.model.dto.UserResponseDto;
import com.hackathonorganizer.userwriteservice.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserResponseDto mapUserToDto(User savedUser) {
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getDescription(),
                savedUser.getKeyCloakId(),
                savedUser.getTags()
        );
    }

}
