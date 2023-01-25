package com.hackathonorganizer.userwriteservice.user.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Entity
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teamId;

    @NotNull
    private Long hackathonId;

    private String info;

    private String entryColor;

    @Builder.Default
    private boolean isAvailable = true;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    private OffsetDateTime sessionStart;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    private OffsetDateTime sessionEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
