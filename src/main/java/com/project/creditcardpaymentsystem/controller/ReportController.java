package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.service.ReportService;
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
@RequestMapping("/reports")
@Tag(name = "Administrative Services")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @PostMapping("/monthly")
    @Operation(summary = "Generate monthly statements metrics dashboard logs profiles datasets entries mappings configurations variables models parameters", tags = {"Administrative Services"})
    public ResponseEntity<?> generateMonthlyReport(
            @Parameter(description = "Customer identity entry identifier key maps parameters tracking indicators profiles", required = true) @RequestParam String customerId,
            @Parameter(description = "Target month values filter context tracking variables configuration profiles arrays setup metrics entries", required = true) @RequestParam int month,
            @Parameter(description = "Target year validation values context requirements frameworks model tracking systems boundaries indices settings", required = true) @RequestParam int year) {
        try {
            // Get the currently authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            // Find the user by username
            User user = userService.findByUsername(userName);
            if (user == null) {
                return new ResponseEntity<>("User  not found.", HttpStatus.NOT_FOUND);
            }

            // Call the report service to generate the report
            reportService.generateMonthlyReport(user.getId(), customerId, month, year);
            return new ResponseEntity<>("Monthly report generated and sent to email.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating report: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}