package com.phinma.docrequest.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.phinma.docrequest.model.User;
import com.phinma.docrequest.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String registerUser(User user) {
        // Enforce the @phinmaed.com domain
        if (user.getEmail() == null || !user.getEmail().trim().toLowerCase().endsWith("@phinmaed.com")) {
            return "Registration failed: Only official @phinmaed.com emails are accepted.";
        }

        if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase())) {
            return "Email is already registered!";
        }

        if (userRepository.existsByStudentIdNumber(user.getStudentIdNumber().trim())) {
            return "Student ID number already exists!";
        }

        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setRole(user.getRole() == null || user.getRole().isEmpty() ? "STUDENT" : user.getRole());
        userRepository.save(user);
        return "SUCCESS";
    }

    public Optional<User> authenticate(String email, String password) {
        if (email == null || !email.trim().toLowerCase().endsWith("@phinmaed.com")) {
            return Optional.empty();
        }
        Optional<User> user = userRepository.findByEmail(email.trim().toLowerCase());
        // FIXED: added .get() to unwrap the Optional
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    public void updatePassword(Long userId, String newPassword) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setPassword(newPassword);
            userRepository.save(u);
        });
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean resetPasswordByEmail(String email, String newPassword) {
        Optional<User> opt = userRepository.findByEmail(email.toLowerCase().trim());
        if (opt.isPresent()) {
            User u = opt.get();
            u.setPassword(newPassword);
            userRepository.save(u);
            return true;
        }
        return false;
    }
}