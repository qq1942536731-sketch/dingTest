package com.example.couponadmin.service;

import com.example.couponadmin.entity.Menu;
import com.example.couponadmin.entity.Role;
import com.example.couponadmin.repository.MenuRepository;
import com.example.couponadmin.repository.RoleRepository;
import com.example.couponadmin.util.PaginationHelper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;

    public RoleService(RoleRepository roleRepository, MenuRepository menuRepository) {
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> findRoles(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return roleRepository.findAll().stream()
                .filter(role -> normalized.isEmpty()
                        || role.getCode().toLowerCase(Locale.ROOT).contains(normalized)
                        || role.getName().toLowerCase(Locale.ROOT).contains(normalized))
                .collect(Collectors.toList());
    }

    public Page<Role> findRolePage(String keyword, int page, int size) {
        return PaginationHelper.paginate(findRoles(keyword), page, size);
    }

    public List<Menu> findAllMenus() {
        return menuRepository.findAllByOrderBySortOrderAsc();
    }

    public List<Menu> findMenus(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return findAllMenus().stream()
                .filter(menu -> normalized.isEmpty()
                        || menu.getName().toLowerCase(Locale.ROOT).contains(normalized)
                        || menu.getPath().toLowerCase(Locale.ROOT).contains(normalized)
                        || menu.getPermission().toLowerCase(Locale.ROOT).contains(normalized))
                .collect(Collectors.toList());
    }

    public Page<Menu> findMenuPage(String keyword, int page, int size) {
        return PaginationHelper.paginate(findMenus(keyword), page, size);
    }

    @Transactional
    public Role createRole(String code, String name, List<Long> menuIds) {
        String normalizedCode = requireText(code, "角色编码不能为空").toUpperCase(Locale.ROOT);
        if (roleRepository.existsByCode(normalizedCode)) {
            throw new IllegalArgumentException("角色编码已存在");
        }
        Role role = new Role();
        role.setCode(normalizedCode);
        role.setName(requireText(name, "角色名称不能为空"));
        role.setMenus(resolveMenus(menuIds));
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, String name, List<Long> menuIds) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        role.setName(requireText(name, "角色名称不能为空"));
        role.setMenus(resolveMenus(menuIds));
        return role;
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("角色不存在");
        }
        roleRepository.deleteById(id);
    }

    private Set<Menu> resolveMenus(List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return new LinkedHashSet<Menu>();
        }
        return new LinkedHashSet<Menu>(menuRepository.findAllById(menuIds));
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
