package com.phinma.docrequest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.repository.UserRepository;

@SpringBootApplication
public class DocumentRequestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentRequestApplication.class, args);
    }

    // Automatically inserts default Admin and Student accounts on startup
    @Bean
    CommandLineRunner initDefaultAccounts(UserRepository userRepository) {
        return args -> {
            // Default Admin Account (School Registrar)
            if (!userRepository.existsByEmail("admin@phinmaed.com")) {
                User admin = new User(
                    "School Registrar Admin",
                    "EMP-2026-001",
                    "admin@phinmaed.com",
                    "admin123",
                    "ADMIN"
                );
                userRepository.save(admin);
            }

            // Default Student Account for instant testing
            if (!userRepository.existsByEmail("student@phinmaed.com")) {
                User student = new User(
                    "Jeffrey Serrano",
                    "03-2425-00123",
                    "student@phinmaed.com",
                    "student123",
                    "STUDENT"
                );
                userRepository.save(student);
            }
        };
    }
}