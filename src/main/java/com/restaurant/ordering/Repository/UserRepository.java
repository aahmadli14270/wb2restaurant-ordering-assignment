package com.restaurant.ordering.Repository;

import com.restaurant.ordering.Model.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // This works because it's on the User entity
}