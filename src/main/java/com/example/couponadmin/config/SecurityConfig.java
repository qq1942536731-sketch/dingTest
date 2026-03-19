package com.example.couponadmin.config;

import com.example.couponadmin.security.DatabaseUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DatabaseUserDetailsService userDetailsService) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**", "/login").permitAll()
                .antMatchers("/api/activities/*/issue").hasAuthority("activity:issue")
                .antMatchers("/activities/**").hasAuthority("activity:view")
                .antMatchers("/users/**").hasAuthority("user:view")
                .antMatchers("/roles/**").hasAuthority("role:view")
                .antMatchers("/menus/**").hasAuthority("menu:view")
                .anyRequest().authenticated()
            .and()
                .userDetailsService(userDetailsService)
                .formLogin().loginPage("/login").defaultSuccessUrl("/", true).permitAll()
            .and()
                .logout().logoutSuccessUrl("/login?logout")
            .and()
                .csrf().ignoringAntMatchers("/api/**");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
