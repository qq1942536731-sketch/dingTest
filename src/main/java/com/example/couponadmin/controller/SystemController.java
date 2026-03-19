package com.example.couponadmin.controller;

import com.example.couponadmin.service.AdminUserService;
import com.example.couponadmin.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
    public String users(@RequestParam(required = false, defaultValue = "") String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("users", adminUserService.findAll(keyword));
        model.addAttribute("roles", roleService.findAllRoles());
        return "users";
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('user:view')")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String displayName,
                             @RequestParam(defaultValue = "true") Boolean enabled,
                             @RequestParam(name = "roleIds", required = false) List<Long> roleIds,
                             RedirectAttributes redirectAttributes) {
        try {
            adminUserService.createUser(username, password, displayName, enabled, roleIds);
            redirectAttributes.addFlashAttribute("success", "用户已创建");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String displayName,
                             @RequestParam(defaultValue = "true") Boolean enabled,
                             @RequestParam(name = "roleIds", required = false) List<Long> roleIds,
                             RedirectAttributes redirectAttributes) {
        try {
            adminUserService.updateUser(id, displayName, enabled, roleIds);
            redirectAttributes.addFlashAttribute("success", "用户已更新");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/delete")
    @PreAuthorize("hasAuthority('user:view')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminUserService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "用户已删除");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('role:view')")
    public String roles(@RequestParam(required = false, defaultValue = "") String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("roles", roleService.findRoles(keyword));
        model.addAttribute("menus", roleService.findAllMenus());
        return "roles";
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('role:view')")
    public String createRole(@RequestParam String code,
                             @RequestParam String name,
                             @RequestParam(name = "menuIds", required = false) List<Long> menuIds,
                             RedirectAttributes redirectAttributes) {
        try {
            roleService.createRole(code, name, menuIds);
            redirectAttributes.addFlashAttribute("success", "角色已创建");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/roles";
    }

    @PostMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public String updateRole(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam(name = "menuIds", required = false) List<Long> menuIds,
                             RedirectAttributes redirectAttributes) {
        try {
            roleService.updateRole(id, name, menuIds);
            redirectAttributes.addFlashAttribute("success", "角色已更新");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/roles";
    }

    @PostMapping("/roles/{id}/delete")
    @PreAuthorize("hasAuthority('role:view')")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", "角色已删除");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/roles";
    }

    @GetMapping("/menus")
    @PreAuthorize("hasAuthority('menu:view')")
    public String menus(@RequestParam(required = false, defaultValue = "") String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("menus", roleService.findMenus(keyword));
        return "menus";
    }
}
