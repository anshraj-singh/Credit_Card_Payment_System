package com.project.creditcardpaymentsystem.service;

import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Handle immutable/empty roles configuration
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            List<String> userRoles = new ArrayList<>();
            userRoles.add("USER");
            user.setRoles(userRoles);
        }
        userRepository.save(user);
    }

    public void saveNewUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getById(String myId) {
        return userRepository.findById(myId);
    }

    public void deleteById(String myId) {
        userRepository.deleteById(myId);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // New method to generate password reset token
    public String generatePasswordResetToken(String username) {
        // Find the user by username
        User user = findByUsername(username);
        // Check if the user exists
        if (user != null) {
            // Generate a new token
            String token = UUID.randomUUID().toString();
            // Set the reset token and expiration time
            user.setResetToken(token);
            user.setResetTokenExpiration(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
            // Save the user with the new token and expiration
            saveNewUser(user);
            return token;
        }
        // Return null if the user is not found
        return null;
    }

    // New method to validate the reset token
    public boolean validateResetToken(String token) {
        User user = userRepository.findByResetToken(token);
        return user != null && user.getResetTokenExpiration().isAfter(LocalDateTime.now());
    }

    // New method to reset the password
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            saveNewUser (user);

            // Send email with updated username and new password
            String subject = "Your Password Has Been Updated";
            String body = "Hello " + user.getUsername() + ",\n\n" +
                    "Your password has been successfully updated.\n" +
                    "Your new password is: " + newPassword + "\n\n" +
                    "If you did not request this change, please contact support.";

            if (user.getCustomers() != null && !user.getCustomers().isEmpty()) {
                emailService.sendTransactionNotification(user.getCustomers().get(0).getEmail(), subject, body);
            }
        }
    }
}