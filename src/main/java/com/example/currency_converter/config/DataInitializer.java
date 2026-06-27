package com.example.currency_converter.config;

import com.example.currency_converter.model.User;
import com.example.currency_converter.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsById("testuser")) {
                User user = new User("testuser", "my-secret-api-key");
                userRepository.save(user);
            }
        };
    }
}
