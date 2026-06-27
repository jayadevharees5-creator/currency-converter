package com.example.currency_converter.config;

import com.example.currency_converter.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Production-level Security Configuration.
 *
 * Public endpoints (no auth required):
 *   /, /swagger-ui/**, /v3/api-docs/**, /h2-console/**, /index.html, /css/**, /js/**
 *
 * Protected endpoints (HTTP Basic Auth):
 *   /api/** (currencies, convert, history)
 *
 * Security improvements over the original:
 *   - BCryptPasswordEncoder instead of NoOpPasswordEncoder
 *   - CORS configuration for frontend
 *   - Proper public/private endpoint split
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS for frontend requests
            .cors(Customizer.withDefaults())

            // Disable CSRF for REST APIs (we use Basic Auth, not cookies)
            .csrf(csrf -> csrf.disable())

            // Allow H2 Console frames
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()))

            // Endpoint authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/").permitAll()
                .requestMatchers("/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // All /api/** endpoints require authentication
                .anyRequest().authenticated()
            )

            // Use HTTP Basic Authentication
            .httpBasic(Customizer.withDefaults())

            // Use our custom UserDetailsService
            .userDetailsService(userDetailsService);

        return http.build();
    }

    /**
     * BCryptPasswordEncoder — industry-standard password hashing.
     * BCrypt automatically salts passwords, making rainbow table attacks useless.
     * Replaces the insecure NoOpPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS configuration — allows the frontend to call the backend API.
     * Without this, browsers block cross-origin requests.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
