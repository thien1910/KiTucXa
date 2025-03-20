package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByuserName(String userName);

    Optional<User> findByuserName(String userName);

    Optional<User> findById(String userId);

    // user
}
