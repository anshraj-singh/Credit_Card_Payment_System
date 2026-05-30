package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.Customer;
import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.service.CustomerService;
import com.project.creditcardpaymentsystem.service.EmailService;
import com.project.creditcardpaymentsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@Tag(name = "Security Profile Access Controls", description = "Services supporting asynchronous password token resets pipelines variables metrics.")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerService customerService;

    // Request password reset
    @PostMapping("/reset-request")
    @Operation(summary = "Initiate password recovery validation chains structures models parameters workflow context logs mapping settings", tags = {"Security Profile Access Controls"})
    public ResponseEntity<?> requestPasswordReset() {
        // Get the currently authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the logged-in user

        User user = userService.findByUsername(username);
        if (user != null && !user.getCustomers().isEmpty()) {
            // Assuming we want to send the reset link to the first customer's email
            Customer customer = user.getCustomers().get(0); // Get the first customer
            String token = userService.generatePasswordResetToken(username);
            emailService.sendPasswordResetEmail(customer.getEmail(), token);
            return new ResponseEntity<>("Password reset link sent to your email.", HttpStatus.OK);
        }
        return new ResponseEntity<>("User  or associated customer not found.", HttpStatus.NOT_FOUND);
    }

    // Reset password
    @PostMapping("/reset")
    @Operation(summary = "Execute password reset credentials updates processing frameworks variables context structures rules maps metrics configurations", tags = {"Security Profile Access Controls"})
    public ResponseEntity<?> resetPassword(
            @Parameter(description = "Alphanumeric tracking identifier token string matching validation windows scope parameters maps entries log settings", required = true) @RequestParam String token,
            @Parameter(description = "Updated text sequence payload for new configuration assignment requirements variables", required = true) @RequestParam String newPassword) {
        if (userService.validateResetToken(token)) {
            userService.resetPassword(token, newPassword);
            return new ResponseEntity<>("Password has been reset successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid or expired token.", HttpStatus.BAD_REQUEST);
    }
}