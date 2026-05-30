package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.security.JwtUtil;
import com.project.creditcardpaymentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (userService.findByUsername(user.getUsername()) != null) {
                return new ResponseEntity<>("Username already taken", HttpStatus.BAD_REQUEST);
            }
            userService.saveUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> authenticationRequest) {
        try {
            String username = authenticationRequest.get("username");
            String password = authenticationRequest.get("password");

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            final String jwt = jwtUtil.generateToken(userDetails.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}