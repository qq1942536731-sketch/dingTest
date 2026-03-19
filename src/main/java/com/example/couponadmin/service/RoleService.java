package com.example.couponadmin.service;

import com.example.couponadmin.entity.Menu;
import com.example.couponadmin.entity.Role;
import com.example.couponadmin.repository.MenuRepository;
import com.example.couponadmin.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Menu> findAllMenus() {
        return menuRepository.findAllByOrderBySortOrderAsc();
    }
}
