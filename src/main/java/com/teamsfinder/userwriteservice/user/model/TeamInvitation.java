package com.teamsfinder.userwriteservice.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RequiredArgsConstructor
@Setter
@Getter
@Entity(name = "user_team_invitations")
public class TeamInvitation extends Invitation {

    @NotEmpty
    private String teamName;

    @NotNull
    private Long teamId;

    @ManyToOne
    User user;
}
