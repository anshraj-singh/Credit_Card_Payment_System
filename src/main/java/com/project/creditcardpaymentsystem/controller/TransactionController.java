package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.Transaction;
import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.service.CreditCardService;
import com.project.creditcardpaymentsystem.service.CustomerService;
import com.project.creditcardpaymentsystem.service.TransactionService;
import com.project.creditcardpaymentsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction processing Engine", description = "Pipeline architectures tracking user card processing workflows verification boundaries logs indexes ledger strings analytics structures components.")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CreditCardService creditCardService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Fetch transaction log registry files entries historical structural logs setup parameters maps metrics context matrices profiles", tags = {"Transaction processing Engine"})
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> allTransactions = transactionService.findAllTransactions();
        if (!allTransactions.isEmpty()) {
            return new ResponseEntity<>(allTransactions, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Publish transactional validation execution packets entries parameters tracking requirements maps models data wrappers setups", tags = {"Transaction processing Engine"})
    public ResponseEntity<?> createTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Core financial execution schema specification entity structures fields updates contexts.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Transaction.class), examples = @ExampleObject(value = "{\n  \"creditCardId\": \"65cba1234567890abcdef\",\n  \"amount\": 1250.75,\n  \"currency\": \"USD\",\n  \"description\": \"Online Retail Purchase Payment Gateway Verification System Log Entry Check.\",\n  \"type\": \"purchase\"\n}"))
            )
            @RequestBody Transaction transaction) {
        // Business logic execution sequence blocks...
        try {
            transaction.setDueDate(LocalDateTime.now().plusDays(30));
            transactionService.saveTransaction(transaction);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating transaction: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    @Operation(summary = "Pull transactional record metrics matching physical document identity indicators fields data settings logs structures systems parameters values maps", tags = {"Transaction processing Engine"})
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String myId) {
        Optional<Transaction> transaction = transactionService.getById(myId);
        return transaction.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("id/{myId}")
    @Operation(summary = "Modify ledger item record attributes flags variables parameters values configurations datasets profiles configurations context settings logs", tags = {"Transaction processing Engine"})
    public ResponseEntity<?> updateTransaction(
            @PathVariable String myId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updatable components template matching target item entry criteria metrics mappings maps log setups profiles options frameworks definitions.",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = "{\n  \"amount\": 1500.00,\n  \"status\": \"COMPLETED\"\n}"))
            )
            @RequestBody Transaction updatedTransaction) {
        Transaction existingTransaction = transactionService.getById(myId).orElse(null);
        if (existingTransaction != null) {
            if (updatedTransaction.getAmount() > 0) {
                existingTransaction.setAmount(updatedTransaction.getAmount());
            }
            if (updatedTransaction.getStatus() != null && !updatedTransaction.getStatus().isEmpty()) {
                existingTransaction.setStatus(updatedTransaction.getStatus());
            }
            transactionService.saveTransaction(existingTransaction);
            return new ResponseEntity<>(existingTransaction, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myId}")
    @Operation(summary = "Purge unique processing entries logs histories systems metadata data values parameters validation criteria maps metrics setups profiles", tags = {"Transaction processing Engine"})
    public ResponseEntity<?> deleteTransaction(@PathVariable String myId) {
        transactionService.deleteById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Search transactions by year range and amount
    @GetMapping("/search")
    @Operation(summary = "Filter chronological payment indexes ranges constraints mapping arrays configurations definitions variables contexts datasets parameters logs", tags = {"Transaction processing Engine"})
    public ResponseEntity<List<Transaction>> searchTransactions(
            @Parameter(description = "ISO local timestamp boundary standard format tracking indicator entry (e.g., '2026-01-01T00:00:00')", required = true) @RequestParam String startDate,
            @Parameter(description = "ISO local timestamp closing limits parameters context criteria maps setups profiles (e.g., '2026-12-31T23:59:59')", required = true) @RequestParam String endDate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user != null) {
            List<Transaction> allTransactions = transactionService.findAllTransactions();
            List<Transaction> filteredTransactions = allTransactions.stream()
                    .filter(transaction -> {
                        LocalDateTime transactionDate = transaction.getTransactionDate();
                        LocalDateTime start = LocalDateTime.parse(startDate);
                        LocalDateTime end = LocalDateTime.parse(endDate);
                        return (transactionDate.isEqual(start) || transactionDate.isAfter(start)) &&
                                (transactionDate.isEqual(end) || transactionDate.isBefore(end)) &&
                                user.getCustomers().stream()
                                        .flatMap(customer -> customer.getTransactionIds().stream())
                                        .anyMatch(id -> id.equals(transaction.getId()));
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(filteredTransactions, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/impact/{transactionId}")
    @Operation(summary = "Analyze historical utilization indexes ratios impacts indicators parameters data variables parameters values records tracking system profiles frameworks maps", tags = {"Transaction processing Engine"})
    public ResponseEntity<String> getTransactionImpact(@PathVariable String transactionId) {
        String impactAnalysis = transactionService.analyzeTransactionImpact(transactionId);
        return new ResponseEntity<>(impactAnalysis, HttpStatus.OK);
    }
}
