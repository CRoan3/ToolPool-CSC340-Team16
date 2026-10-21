package com.csc340.toolpool.repository;

import com.csc340.toolpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Data access for the users table. JpaRepository provides save, findById, findAll, deleteById.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring writes the SQL from the name: "does any user have this email, ignoring
    // case?"
    boolean existsByEmailIgnoreCase(String email);

    // All users with one account status (e.g. BANNED), for the admin user list.
    List<User> findByAccountStatusOrderByIdAsc(String accountStatus);

    List<User> findAllByOrderByIdAsc();
}