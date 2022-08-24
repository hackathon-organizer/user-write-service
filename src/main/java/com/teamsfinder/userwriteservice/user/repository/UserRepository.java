package com.teamsfinder.userwriteservice.user.repository;

import com.teamsfinder.userwriteservice.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
