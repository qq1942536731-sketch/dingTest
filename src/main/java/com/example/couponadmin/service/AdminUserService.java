package com.example.couponadmin.service;

import com.example.couponadmin.entity.AdminUser;
import com.example.couponadmin.entity.Menu;
import com.example.couponadmin.entity.Role;
import com.example.couponadmin.repository.AdminUserRepository;
import com.example.couponadmin.repository.RoleRepository;
import com.example.couponadmin.util.PaginationHelper;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserService {
    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AdminUserRepository adminUserRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<AdminUser> findByUsername(String username) {
        return adminUserRepository.findByUsername(username);
    }

    public List<AdminUser> findAll() {
        return findAll("");
    }

    public List<AdminUser> findAll(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return adminUserRepository.findAll().stream()
                .filter(user -> normalized.isEmpty()
                        || user.getUsername().toLowerCase(Locale.ROOT).contains(normalized)
                        || user.getDisplayName().toLowerCase(Locale.ROOT).contains(normalized))
                .sorted(Comparator.comparing(AdminUser::getId))
                .collect(Collectors.toList());
    }

    public Page<AdminUser> findPage(String keyword, int page, int size) {
        return PaginationHelper.paginate(findAll(keyword), page, size);
    }

    public AdminUser findById(Long id) {
        return adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    @Transactional
    public AdminUser createUser(String username, String password, String displayName, Boolean enabled, List<Long> roleIds) {
        String normalizedUsername = requireText(username, "用户名不能为空");
        if (findByUsername(normalizedUsername).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        AdminUser user = new AdminUser();
        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(requireText(password, "密码不能为空")));
        user.setDisplayName(requireText(displayName, "显示名不能为空"));
        user.setEnabled(enabled != null ? enabled : Boolean.TRUE);
        user.setRoles(resolveRoles(roleIds));
        return adminUserRepository.save(user);
    }

    @Transactional
    public AdminUser updateUser(Long id, String displayName, Boolean enabled, List<Long> roleIds) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setDisplayName(requireText(displayName, "显示名不能为空"));
        user.setEnabled(enabled != null ? enabled : Boolean.FALSE);
        user.setRoles(resolveRoles(roleIds));
        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!adminUserRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }
        adminUserRepository.deleteById(id);
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

    private Set<Role> resolveRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new LinkedHashSet<Role>();
        }
        return new LinkedHashSet<Role>(roleRepository.findAllById(roleIds));
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
