package com.example.couponadmin.repository;

import com.example.couponadmin.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Override
    @EntityGraph(attributePaths = "menus")
    List<Role> findAll();

    boolean existsByCode(String code);
}
