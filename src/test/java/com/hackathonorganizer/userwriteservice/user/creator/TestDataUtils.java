package com.hackathonorganizer.userwriteservice.user.creator;


import com.hackathonorganizer.userwriteservice.user.model.AccountType;
import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import com.hackathonorganizer.userwriteservice.user.model.User;
import com.hackathonorganizer.userwriteservice.user.model.dto.ScheduleEntryRequest;
import com.hackathonorganizer.userwriteservice.user.repository.ScheduleEntryRepository;
import com.hackathonorganizer.userwriteservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataUtils {

    private final UserRepository userRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;

    public User createUser() {
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

    public User createUserWithKeycloakId(String keycloakId) {
        UUID uuid = UUID.randomUUID();
        return userRepository.save(User.builder()
                .id(null)
                .username(uuid.toString())
                .keyCloakId(keycloakId)
                .accountType(AccountType.USER)
                .githubProfileUrl("USER_GITHUB")
                .profilePictureUrl("USER_PICTURE")
                .currentTeamId(5L)
                .currentHackathonId(1L)
                .tags(new HashSet<>())
                .build());
    }

    public static User buildMockUser() {
        return User.builder()
                .id(1L)
                .keyCloakId("id")
                .accountType(AccountType.USER)
                .blocked(false)
                .scheduleEntries(Set.of())
                .tags(new HashSet<>())
                .build();
    }

    public ScheduleEntry createScheduleEntry(User user) {
        return scheduleEntryRepository.save(new ScheduleEntry(
                null,
                5L,
                20L,
                "info",
                "red",
                true,
                OffsetDateTime.of(2123, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2123, 10, 10, 16, 0, 0, 0, ZoneOffset.UTC),
                user
        ));
    }

    public static ScheduleEntry buildScheduleEntry(User user) {
        return new ScheduleEntry(1L,
                5L,
                10L,
                "info",
                "red",
                true,
                OffsetDateTime.of(2123, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2123, 10, 10, 16, 0, 0, 0, ZoneOffset.UTC),
                user);
    }

    public static ScheduleEntryRequest buildScheduleEntryRequest() {
        return new ScheduleEntryRequest(
                null,
                5L,
                "info",
                "red",
                OffsetDateTime.of(2123, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2123, 10, 10, 16, 0, 0, 0, ZoneOffset.UTC));
    }

    public void updateScheduleEntry(ScheduleEntry scheduleEntry) {
        scheduleEntryRepository.save(scheduleEntry);
    }
}
