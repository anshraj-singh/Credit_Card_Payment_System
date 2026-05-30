package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.CardReplacementRequest;
import com.project.creditcardpaymentsystem.entity.Customer;
import com.project.creditcardpaymentsystem.entity.Transaction;
import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.service.CardReplacementService;
import com.project.creditcardpaymentsystem.service.CustomerService;
import com.project.creditcardpaymentsystem.service.ReportService;
import com.project.creditcardpaymentsystem.service.TransactionService;
import com.project.creditcardpaymentsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administrative Services", description = "Platform management operations. Requires a valid token with ROLE_ADMIN authority context.")
public class AdminController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CardReplacementService cardReplacementService;

    // Get all customers - Only accessible by admin
    @GetMapping("/all-customers")
    @Operation(
            summary = "View all registered platform customers",
            description = "Retrieves an unbounded complete data array listing of registered customer accounts.",
            tags = {"Administrative Services"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Query lookup execution success",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
            @ApiResponse(responseCode = "404", description = "No customer entries populate system records")
    })
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> allCustomers = customerService.findAllCustomers();
        if (allCustomers != null && !allCustomers.isEmpty()) {
            return new ResponseEntity<>(allCustomers, HttpStatus.OK);
        }
        return new ResponseEntity<>("No customers found.", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/reports/monthly")
    @Operation(
            summary = "Trigger structural monthly customer statement email",
            description = "Compiles cross-referenced card logs dynamically based on criteria parameters and sends a structured notification email.",
            tags = {"Administrative Services"}
    )
    public ResponseEntity<?> generateMonthlyReport(
            @Parameter(description = "Target database document identifier string for unique customer mapping", required = true) @RequestParam String customerId,
            @Parameter(description = "Numeric month value filter scope (e.g., 5 for May)", required = true) @RequestParam int month,
            @Parameter(description = "Numeric year context scope (e.g., 2026)", required = true) @RequestParam int year) {
        try {
            // Get the currently authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            // Find the user by username
            User user = userService.findByUsername(userName);
            if (user == null) {
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }

            // Call the report service to generate the report
            reportService.generateMonthlyReport(user.getId(), customerId, month, year);
            return new ResponseEntity<>("Monthly report generated and sent to email.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating report: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // New endpoint to view all transactions
    @GetMapping("/transactions")
    @Operation(
            summary = "Audit global platform transaction ledgers",
            description = "Fetches payment histories uniformly tracked across all system credit card mappings. Requires admin privileges.",
            tags = {"Administrative Services"}
    )
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> allTransactions = transactionService.findAllTransactions();
        if (!allTransactions.isEmpty()) {
            return new ResponseEntity<>(allTransactions, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // New endpoint to manage card replacement requests
    @GetMapping("/card-replacements")
    @Operation(
            summary = "Fetch card replacement query backlog status logs",
            description = "Collects system requests tracking card hardware asset replacement inquiries matching structural tracking flags.",
            tags = {"Administrative Services"}
    )
    public ResponseEntity<List<CardReplacementRequest>> getAllCardReplacementRequests() {
        List<CardReplacementRequest> allRequests = cardReplacementService.findAllRequests(); // Ensure this method exists in CardReplacementService
        if (allRequests != null && !allRequests.isEmpty()) {
            return new ResponseEntity<>(allRequests, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}