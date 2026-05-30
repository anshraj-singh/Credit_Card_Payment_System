package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.Customer;
import com.project.creditcardpaymentsystem.entity.Transaction;
import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.service.CustomerService;
import com.project.creditcardpaymentsystem.service.EmailService;
import com.project.creditcardpaymentsystem.service.TransactionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Ledger Profiles", description = "Operations tracking metadata parameters, links, and transactional aggregations contextualized to clients accounts entities frameworks.")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Onboard individual client database accounts entities files", tags = {"Customer Ledger Profiles"})
    public ResponseEntity<?> createCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer identity entry core specification template tracking arrays data definition mappings models.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Customer.class), examples = @ExampleObject(value = "{\n  \"name\": \"Ansh Raj Singh\",\n  \"email\": \"ansh@example.com\",\n  \"phone\": \"+919876543210\",\n  \"address\": \"Gwalior, Madhya Pradesh, India\"\n}"))
            )
            @RequestBody Customer myCustomer) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUsername(userName);
            customerService.saveNewCustomer(myCustomer, userName);

            // Send confirmation email
            String subject = "Welcome to the Credit Card Payment System";
            String body = "Dear " + myCustomer.getName() + ",\n\n" +
                    "Your account has been successfully created.\n" +
                    "Username: " + user.getUsername() + "\n\n" +
                    "Thank you for joining us!\n" +
                    "Best regards,\n" +
                    "Credit Card Payment System Team";

            emailService.sendTransactionNotification(myCustomer.getEmail(), subject, body);
            User updateUser = userService.findByUsername(userName);
            return new ResponseEntity<>(updateUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    @Operation(summary = "Query customer fields profile metrics records values configurations", tags = {"Customer Ledger Profiles"})
    public ResponseEntity<Customer> getCustomerById(@PathVariable String myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUsername(userName);

        Optional<Customer> customer = customerService.getById(myId);
        if (customer.isPresent() && user.getCustomers().contains(customer.get())) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("id/{myId}")
    @Operation(summary = "Mutate metadata attributes parameter configurations structural mappings context datasets parameters logs definitions", tags = {"Customer Ledger Profiles"})
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String myId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Target entry payload updates schemas parameters mappings profiles specifications variables data wrappers.",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = "{\n  \"phone\": \"+919999999999\",\n  \"address\": \"New Delhi, India\"\n}"))
            )
            @RequestBody Customer updatedCustomer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUsername(userName);

        Optional<Customer> customerOpt = customerService.getById(myId);
        if (customerOpt.isPresent() && user.getCustomers().contains(customerOpt.get())) {
            Customer existingCustomer = customerOpt.get();
            if (updatedCustomer.getName() != null) {
                existingCustomer.setName(updatedCustomer.getName());
            }
            if (updatedCustomer.getEmail() != null) {
                existingCustomer.setEmail(updatedCustomer.getEmail());
            }
            if (updatedCustomer.getPhone() != null) {
                existingCustomer.setPhone(updatedCustomer.getPhone());
            }
            if (updatedCustomer.getAddress() != null) {
                existingCustomer.setAddress(updatedCustomer.getAddress());
            }
            customerService.saveCustomer(existingCustomer);
            return new ResponseEntity<>(existingCustomer, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myId}")
    @Operation(summary = "Purge customer identity entities links records records values structural parameters settings", tags = {"Customer Ledger Profiles"})
    public ResponseEntity<?> deleteCustomer(@PathVariable String myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        customerService.deleteById(myId,userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // New endpoint to get all transactions for the authenticated user's customers
    @GetMapping("/transactions")
    @Operation(summary = "Compile user transactions metrics graphs data structural values summaries collections context mappings logs", tags = {"Customer Ledger Profiles"})
    public ResponseEntity<?> getAllTransactionsForAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUsername(userName);

        if (user != null) {
            List<Transaction> transactions = transactionService.findAllTransactions()
                    .stream()
                    .filter(transaction -> transaction.getCreditCardId() != null &&
                            user.getCustomers().stream()
                                    .flatMap(customer -> customer.getCreditCardIds().stream())
                                    .anyMatch(id -> id.equals(transaction.getCreditCardId())))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(transactions, HttpStatus.OK);
        }
        return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
    }
}