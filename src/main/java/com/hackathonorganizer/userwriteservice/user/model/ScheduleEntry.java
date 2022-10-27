package com.hackathonorganizer.userwriteservice.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@ToString
public class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long teamId;

    @NotNull
    Long hackathonId;

    String info;

    String entryColor;

    boolean isAvailable;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    LocalDateTime sessionStart;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    LocalDateTime sessionEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    User user;
}
