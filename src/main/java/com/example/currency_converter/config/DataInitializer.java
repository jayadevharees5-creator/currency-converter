package com.example.currency_converter.config;

import com.example.currency_converter.model.User;
import com.example.currency_converter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Initializes the database with default data on startup.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${app.default-user.username}")
    private String defaultUsername;

    @Value("${app.default-user.password}")
    private String defaultPassword;

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsById(defaultUsername)) {
                log.info("Creating default user '{}' with BCrypt hashed password", defaultUsername);

                // Hash the password before saving to the database
                String hashedPassword = passwordEncoder.encode(defaultPassword);

                User user = new User(defaultUsername, hashedPassword, "USER");
                userRepository.save(user);

                log.info("Default user created successfully. Use these credentials to test the API.");
            } else {
                log.info("Default user '{}' already exists in the database.", defaultUsername);
            }
        };
    }
}
