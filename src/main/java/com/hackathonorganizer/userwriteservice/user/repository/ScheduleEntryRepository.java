package com.hackathonorganizer.userwriteservice.user.repository;

import com.hackathonorganizer.userwriteservice.user.model.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {

}