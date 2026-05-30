package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.security.JwtUtil;
import com.project.creditcardpaymentsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication Portal", description = "Public gateways for client onboarding and generating JWT Bearer access tokens.")
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
    @Operation(
            summary = "Register a new profile",
            description = "Saves user credentials with secure BCrypt encryption. Defaults role assignment to standard 'USER'. Open public access.",
            tags = {"Authentication Portal"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User profile provisioned successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload processing error or username already taken")
    })
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration credentials schema packet",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(
                                    value = "{\n  \"username\": \"ansh_raj\",\n  \"password\": \"SecurePass123!\"\n}"
                            )
                    )
            )
            @RequestBody User user) {
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
    @Operation(
            summary = "Authenticate user credentials",
            description = "Validates client credentials. Returns a stateless bearer JWT token string valid for 60 minutes.",
            tags = {"Authentication Portal"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token issued successfully",
                    content = @Content(examples = @ExampleObject(value = "{\n  \"token\": \"eyJhbGciOiJIUzI1NiJ9...\"\n}"))),
            @ApiResponse(responseCode = "401", description = "Bad authentication request / Bad credentials")
    })
    public ResponseEntity<?> loginUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Username and plain text raw password object parameters",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\n  \"username\": \"ansh_raj\",\n  \"password\": \"SecurePass123!\"\n}"
                            )
                    )
            )
            @RequestBody Map<String, String> authenticationRequest) {
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