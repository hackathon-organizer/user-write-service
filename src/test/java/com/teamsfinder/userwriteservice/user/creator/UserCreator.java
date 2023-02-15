package com.teamsfinder.userwriteservice.user.creator;


import com.hackathonorganizer.userwriteservice.user.model.AccountType;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserCreator {

    private final UserRepository userRepository;

    public User create() {
        UUID uuid = UUID.randomUUID();
        return userRepository.save(User.builder()
                .id(null)
                .username(uuid.toString())
                .keyCloakId(uuid.toString())
                .accountType(AccountType.USER)
                .githubProfileUrl("USER_GITHUB")
                .profilePictureUrl("USER_PICTURE")
                .blocked(false)
                .tags(new HashSet<>())
                .build());
    }

    public User createWithKeycloakId(String keycloakId) {
        UUID uuid = UUID.randomUUID();
        return userRepository.save(User.builder()
                .id(null)
                .username(uuid.toString())
                .keyCloakId(keycloakId)
                .accountType(AccountType.USER)
                .githubProfileUrl("USER_GITHUB")
                .profilePictureUrl("USER_PICTURE")
                .blocked(false)
                .tags(new HashSet<>())
                .build());
    }
}
