package com.teamsfinder.userwriteservice.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Long invitingUserId;

    @Enumerated(EnumType.STRING)
    InvitationStatus invitationStatus;
}
