
package com.example.hosteltracker;

import com.example.hosteltracker.model.UserEntity;
import com.example.hosteltracker.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserAccountInitializer {

    @Bean
    CommandLineRunner createUserAccounts(
            UserRepository repository,
            PasswordEncoder encoder) {

        return args -> {

            // Four students
            createAccount(repository, encoder,
                    "stud@1234", "Student#1234!", "STUDENT");

            createAccount(repository, encoder,
                    "stud@1235", "Student#1235!", "STUDENT");

            createAccount(repository, encoder,
                    "stud@1236", "Student#1236!", "STUDENT");

            createAccount(repository, encoder,
                    "stud@1237", "Student#1237!", "STUDENT");


            // Three Resident Counsellors
            createAccount(repository, encoder,
                    "rc@1001", "RC#1001!", "RC");

            createAccount(repository, encoder,
                    "rc@1002", "RC#1002!", "RC");

            createAccount(repository, encoder,
                    "rc@1003", "RC#1003!", "RC");


            // Three Staff accounts
            createAccount(repository, encoder,
                    "staff@2001", "Staff#2001!", "STAFF");

            createAccount(repository, encoder,
                    "staff@2002", "Staff#2002!", "STAFF");

            createAccount(repository, encoder,
                    "staff@2003", "Staff#2003!", "STAFF");
        };
    }

    private void createAccount(
            UserRepository repository,
            PasswordEncoder encoder,
            String loginId,
            String rawPassword,
            String role) {

        if (!repository.existsByLoginId(loginId)) {

            UserEntity account = new UserEntity(
                    loginId,
                    encoder.encode(rawPassword),
                    role
            );

            repository.save(account);

            System.out.println(
                    "Created " + role + " account: " + loginId);
        }
    }
}
