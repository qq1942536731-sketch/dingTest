package com.example.couponadmin.controller;

import com.example.couponadmin.service.AdminUserService;
import com.example.couponadmin.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SystemController {
    private final AdminUserService adminUserService;
    private final RoleService roleService;

    public SystemController(AdminUserService adminUserService, RoleService roleService) {
        this.adminUserService = adminUserService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:view')")
    public String users(Model model) {
        model.addAttribute("users", adminUserService.findAll());
        return "users";
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('role:view')")
    public String roles(Model model) {
        model.addAttribute("roles", roleService.findAllRoles());
        return "roles";
    }

    @GetMapping("/menus")
    @PreAuthorize("hasAuthority('menu:view')")
    public String menus(Model model) {
        model.addAttribute("menus", roleService.findAllMenus());
        return "menus";
    }
}
