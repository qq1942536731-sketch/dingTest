package com.example.couponadmin.security;

import com.example.couponadmin.entity.AdminUser;
import com.example.couponadmin.entity.Menu;
import com.example.couponadmin.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class LoginUser extends User {
    private final String displayName;

    public LoginUser(AdminUser user) {
        super(user.getUsername(), user.getPassword(), Boolean.TRUE.equals(user.getEnabled()), true, true, true, authorities(user));
        this.displayName = user.getDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }

    private static Collection<? extends GrantedAuthority> authorities(AdminUser user) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            for (Menu menu : role.getMenus()) {
                if (menu.getPermission() != null && !menu.getPermission().trim().isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(menu.getPermission()));
                }
            }
        }
        return authorities;
    }
}
