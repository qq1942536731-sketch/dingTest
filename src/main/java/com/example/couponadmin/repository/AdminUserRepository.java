package com.example.couponadmin.repository;

import com.example.couponadmin.entity.AdminUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    @EntityGraph(attributePaths = {"roles", "roles.menus"})
    Optional<AdminUser> findByUsername(String username);
}
