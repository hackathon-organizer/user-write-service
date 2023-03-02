package com.hackathonorganizer.userwriteservice.user.repository;

import com.hackathonorganizer.userwriteservice.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKeyCloakId(String keyCloakId);

    @Query("SELECT u FROM User u JOIN FETCH u.scheduleEntries")
    Optional<User> findUserByIdWithScheduleEntries(Long userId);

    @Query("SELECT u FROM User u JOIN FETCH u.tags")
    Optional<User> findUserByIdWithTags(Long userId);
}
