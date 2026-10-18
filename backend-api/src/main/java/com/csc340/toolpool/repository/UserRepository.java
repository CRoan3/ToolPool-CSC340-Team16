package com.csc340.toolpool.repository;

import com.csc340.toolpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Data access for the users table. JpaRepository provides save, findById, findAll, deleteById.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring writes the SQL from the name: "does any user have this email, ignoring
    // case?"
    boolean existsByEmailIgnoreCase(String email);
}