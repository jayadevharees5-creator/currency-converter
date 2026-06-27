package com.example.currency_converter.service;

import com.example.currency_converter.model.User;
import com.example.currency_converter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom UserDetailsService to load user credentials from the database.
 * Used by Spring Security during HTTP Basic Authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user: {}", username);

        User user = userRepository.findById(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: User {} not found", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        // Use the proper role from the database, prefixing with "ROLE_" as required by Spring Security
        String role = user.getRole() != null ? user.getRole() : "USER";

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // This should be BCrypt hashed in the DB
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
        );
    }
}
