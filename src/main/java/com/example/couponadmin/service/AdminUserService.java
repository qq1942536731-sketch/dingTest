package com.example.couponadmin.service;

import com.example.couponadmin.entity.AdminUser;
import com.example.couponadmin.entity.Menu;
import com.example.couponadmin.repository.AdminUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminUserService {
    private final AdminUserRepository adminUserRepository;

    public AdminUserService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    public Optional<AdminUser> findByUsername(String username) {
        return adminUserRepository.findByUsername(username);
    }

    public List<AdminUser> findAll() {
        return adminUserRepository.findAll();
    }

    public List<Menu> resolveMenus(String username) {
        return findByUsername(username)
                .map(user -> user.getRoles().stream()
                        .flatMap(role -> role.getMenus().stream())
                        .distinct()
                        .sorted(Comparator.comparing(Menu::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<Menu>());
    }
}
