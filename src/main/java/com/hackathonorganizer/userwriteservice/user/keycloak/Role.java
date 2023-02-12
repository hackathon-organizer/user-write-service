package com.hackathonorganizer.userwriteservice.user.keycloak;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
public enum Role {
    ORGANIZER("ORGANIZER"),
    TEAM_OWNER("TEAM_OWNER"),
    MENTOR("MENTOR"),
    JURY("JURY"),
    USER("USER");

    private String role;

    Role(String role) {
        this.role = role;
    }

    @JsonCreator
    public static Role getRoleFromName(String role) {

        return Arrays.stream(Role.values()).filter(r -> r.name().equals(role)).findFirst().orElse(null);
    }
}
