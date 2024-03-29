package com.hackathonorganizer.userwriteservice.user.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
@Slf4j
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String username;

    private String description;

    @NotEmpty
    @Column(updatable = false)
    private String keyCloakId;

    private Long currentHackathonId;

    private Long currentTeamId;

    private boolean blocked;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "user_tags",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ScheduleEntry> scheduleEntries = new HashSet<>();

    public void addScheduleEntry(ScheduleEntry scheduleEntry) {

        if (!scheduleEntries.add(scheduleEntry)) {
            log.info("Schedule entry with id: {} already exist", scheduleEntry.getId());
        }
    }
}
