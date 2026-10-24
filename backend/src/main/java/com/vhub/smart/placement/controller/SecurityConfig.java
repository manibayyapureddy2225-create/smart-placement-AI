package com.vhub.smart.placement.controller;

import com.vhub.smart.placement.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService users(UserRepository repository) {
        return email -> repository.findByEmail(email.trim().toLowerCase(java.util.Locale.ROOT))
            .map(u -> User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole()).build())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/health", "/api/auth/register", "/api/auth/login", "/error").permitAll()
                .requestMatchers("/api/admin/**", "/api/applications/job/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .httpBasic(b -> b.authenticationEntryPoint((req, res, ex) -> {
                res.setStatus(401); res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Authentication required\"}");
            })).build();
    }
}
