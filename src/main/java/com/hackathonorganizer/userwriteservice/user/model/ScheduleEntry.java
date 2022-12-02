package com.hackathonorganizer.userwriteservice.user.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    private boolean isAvailable;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    private LocalDateTime sessionStart;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm dd-MM-YYYY")
    private LocalDateTime sessionEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
