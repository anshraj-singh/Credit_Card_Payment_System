package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/Users")
@Tag(name = "Security Profile Access Controls")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @Operation(summary = "Resolve currently active session profile context structures parameters values mappings logs properties data metrics definitions parameters", tags = {"Security Profile Access Controls"})
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{myId}")
    @Operation(summary = "Query authorization registration attributes metrics identifiers structures criteria profiles data sets validation values constraints", tags = {"Security Profile Access Controls"})
    public ResponseEntity<?> getUserById(@PathVariable String myId) {
        Optional<User> myEntry = userService.getById(myId);
        if (myEntry.isPresent()) {
            return new ResponseEntity<>(myEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update a user
    @PutMapping("/update-user")
    @Operation(summary = "Mutate base security identity registration parameters records updates variables frameworks configurations arrays models values mappings options", tags = {"Security Profile Access Controls"})
    public ResponseEntity<?> updateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated base configuration credential updates entries tracking fields parameters datasets setups logs options metrics boundaries maps values definitions.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(value = "{\n  \"username\": \"ansh_new_profile\",\n  \"password\": \"UpdatedSecurePass123!\"\n}")
                    )
            )
            @RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInDb = userService.findByUsername(username);
        if (userInDb != null) {
            userInDb.setUsername(user.getUsername());
            userInDb.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveNewUser(userInDb);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}