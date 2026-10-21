
package com.example.hosteltracker;

import com.example.hosteltracker.model.UserEntity;
import com.example.hosteltracker.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminAccountInitializer {

    @Bean
    CommandLineRunner createAdminAccounts(
            UserRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            createAdmin(repository, passwordEncoder,
                    "admin@1001", "HC_Admin#1001!");

            createAdmin(repository, passwordEncoder,
                    "admin@1002", "HC_Admin#1002!");

            createAdmin(repository, passwordEncoder,
                    "admin@1003", "HC_Admin#1003!");
        };
    }

    private void createAdmin(
            UserRepository repository,
            PasswordEncoder encoder,
            String loginId,
            String rawPassword) {

        if (!repository.existsByLoginId(loginId)) {
            UserEntity admin = new UserEntity(
                    loginId,
                    encoder.encode(rawPassword),
                    "ADMIN"
            );

            repository.save(admin);
            System.out.println(
                    "Created administrator: " + loginId);
        }
    }
}
