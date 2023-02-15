package com.hackathonorganizer.userwriteservice.utils;

import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.model.dto.UserDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserDto mapToDto(User savedUser) {
        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getDescription(),
                savedUser.getKeyCloakId(),
                savedUser.getTags()
        );
    }

}
