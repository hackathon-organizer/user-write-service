package com.hackathonorganizer.userwriteservice.user.model;

import com.hackathonorganizer.userwriteservice.tag.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String username;

    @NotEmpty
    private String keyCloakId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccountType accountType = AccountType.USER;

    private String githubProfileUrl;

    private String profilePictureUrl;

    private Long currentHackathonId;

    private Long currentTeamId;

    private boolean blocked;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "user_tags",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();
}
