package com.example.couponadmin.controller;

import com.example.couponadmin.service.ActivityService;
import com.example.couponadmin.service.AdminUserService;
import com.example.couponadmin.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ActivityService activityService;
    private final AdminUserService adminUserService;
    private final RoleService roleService;

    public HomeController(ActivityService activityService, AdminUserService adminUserService, RoleService roleService) {
        this.activityService = activityService;
        this.adminUserService = adminUserService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activityCount", activityService.findAll().size());
        model.addAttribute("userCount", adminUserService.findAll().size());
        model.addAttribute("roleCount", roleService.findAllRoles().size());
        return "index";
    }
}
