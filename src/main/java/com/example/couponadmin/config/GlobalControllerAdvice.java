package com.example.couponadmin.config;

import com.example.couponadmin.security.LoginUser;
import com.example.couponadmin.service.AdminUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final AdminUserService adminUserService;

    public GlobalControllerAdvice(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @ModelAttribute("currentDisplayName")
    public String currentDisplayName(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return ((LoginUser) authentication.getPrincipal()).getDisplayName();
        }
        return "";
    }

    @ModelAttribute("grantedMenus")
    public Object grantedMenus(Authentication authentication) {
        if (authentication == null) {
            return java.util.Collections.emptyList();
        }
        return adminUserService.resolveMenus(authentication.getName());
    }
}
