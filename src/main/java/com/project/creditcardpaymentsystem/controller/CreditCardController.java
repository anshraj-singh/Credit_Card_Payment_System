package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.CardReplacementRequest;
import com.project.creditcardpaymentsystem.entity.CreditCard;
import com.project.creditcardpaymentsystem.entity.Customer;
import com.project.creditcardpaymentsystem.entity.User;
import com.project.creditcardpaymentsystem.service.CreditCardService;
import com.project.creditcardpaymentsystem.service.CreditScoreService;
import com.project.creditcardpaymentsystem.service.CustomerService;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/credit-cards")
@Tag(name = "Credit Card Engine", description = "Operations managing lifecycle profiles, limits, locking, and tracking financial credit scoring variables.")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;


    @Autowired
    private CreditScoreService creditScoreService;

    // GET ALL CREDIT CARDS FOR LOGGED-IN USER
    @GetMapping
    @Operation(summary = "Get user credit card portfolios", description = "Resolves token properties to pull underlying account vectors and output connected credit card arrays.", tags = {"Credit Card Engine"})
    public ResponseEntity<List<CreditCard>> getAllCreditCards() {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        if (user != null && !user.getCustomers().isEmpty()) {
            // Retrieve all credit cards of the user's customers
            List<CreditCard> allCreditCards = user.getCustomers()
                    .stream()
                    .flatMap(customer -> customer.getCreditCardIds().stream())
                    .map(creditCardService::getById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            return new ResponseEntity<>(allCreditCards, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // CREATE A NEW CREDIT CARD
    @PostMapping
    @Operation(summary = "Provision a card instance asset record", description = "Appends unique validation structural configuration arrays linking a card payload target matching valid mapping conditions.", tags = {"Credit Card Engine"})
    public ResponseEntity<?> createCreditCard(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credit Card initialization payload entity object fields template parameter context configuration definitions.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreditCard.class),
                            examples = @ExampleObject(value = "{\n  \"cardNumber\": \"4321567890123456\",\n  \"cardHolderName\": \"Ansh Raj Singh\",\n  \"expirationDate\": \"12/29\",\n  \"cvv\": \"333\",\n  \"spendingLimit\": 50000.00,\n  \"cardType\": \"Visa\"\n}")
                    )
            )
            @RequestBody CreditCard creditCard,
            @Parameter(description = "Customer target mapping identification identifier string value matching active user profile entity array parameters", required = true) @RequestParam String customerId) {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        if (user != null) {
            Optional<Customer> customerOptional = user.getCustomers()
                    .stream()
                    .filter(c -> c.getId().equals(customerId))
                    .findFirst();

            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                creditCard.setCustomerId(customerId);

                // Ensure spending limit is set correctly
                if (creditCard.getSpendingLimit() < 0) {
                    return new ResponseEntity<>("Spending limit cannot be negative", HttpStatus.BAD_REQUEST);
                }

                // Save credit card and update the customer
                creditCardService.saveCreditCard(creditCard);
                customer.getCreditCardIds().add(creditCard.getId());
                customerService.saveCustomer(customer);

                return new ResponseEntity<>(creditCard, HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Customer not found", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
    }

    // GET CREDIT CARD BY ID
    @GetMapping("/id/{myId}")
    @Operation(summary = "Get credit card data target properties by entity id matching path parameters constraints values mappings", tags = {"Credit Card Engine"})
    public ResponseEntity<?> getCreditCardById(@PathVariable String myId) {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        if (isCreditCardOwnedByUser(user, myId)) {
            Optional<CreditCard> creditCard = creditCardService.getById(myId);
            return creditCard.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }

    // UPDATE CREDIT CARD
    @PutMapping("/id/{myId}")
    @Operation(summary = "Update credit card registration data records dynamically matching path variable entity parameters values configurations context settings maps", tags = {"Credit Card Engine"})
    public ResponseEntity<?> updateCreditCard(
            @PathVariable String myId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credit card updating entry fields parameters data definition parameters metadata wrapper tracking model profiles specifications logs.",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = "{\n  \"cardHolderName\": \"Ansh R Singh\"\n}"))
            )
            @RequestBody CreditCard updatedCreditCard) {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        if (isCreditCardOwnedByUser(user, myId)) {
            CreditCard existingCreditCard = creditCardService.getById(myId).orElse(null);
            if (existingCreditCard != null) {
                if (updatedCreditCard.getCardHolderName() != null) {
                    existingCreditCard.setCardHolderName(updatedCreditCard.getCardHolderName());
                }
                if (updatedCreditCard.getCardNumber() != null) {
                    existingCreditCard.setCardNumber(updatedCreditCard.getCardNumber());
                }
                if (updatedCreditCard.getExpirationDate() != null) {
                    existingCreditCard.setExpirationDate(updatedCreditCard.getExpirationDate());
                }
                creditCardService.saveCreditCard(existingCreditCard);
                return new ResponseEntity<>(existingCreditCard, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }

    // DELETE CREDIT CARD
    @DeleteMapping("/id/{myId}")
    @Operation(summary = "Purge card instance profiles mapping data sets", description = "Deletes credit card record instances permanently matching unique key identity target maps.", tags = {"Credit Card Engine"})
    public ResponseEntity<?> deleteCreditCard(@PathVariable String myId) {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        if (isCreditCardOwnedByUser(user, myId)) {
            creditCardService.deleteById(myId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }

    // New endpoint to get card benefits
    @GetMapping("/benefits/{cardId}")
    @Operation(summary = "Inspect rewards criteria profiles information rules strings definitions arrays templates structures logic profiles", tags = {"Credit Card Engine"})
    public ResponseEntity<String> getCardBenefits(@PathVariable String cardId) {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        if (user != null && isCreditCardOwnedByUser (user, cardId)) {
            String benefits = creditCardService.getCardBenefits(cardId);
            return new ResponseEntity<>(benefits, HttpStatus.OK);
        }

        return new ResponseEntity<>("Unauthorized access or card not found", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/replace-card")
    @Operation(summary = "File system tracking inquiries logging card asset validation structures", tags = {"Credit Card Engine"})
    public ResponseEntity<String> requestCardReplacement(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Replacement metadata entry parameters schemas payload structure parameters data wrapper configurations log settings profiles arrays.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CardReplacementRequest.class), examples = @ExampleObject(value = "{\n  \"cardId\": \"65cba1234567890abcdef\",\n  \"reason\": \"STOLEN\"\n}"))
            )
            @RequestBody CardReplacementRequest request) {
        String responseMessage = creditCardService.requestCardReplacement(request);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    // Endpoint to get credit card score
    @GetMapping("/score/{cardId}")
    @Operation(summary = "Fetch numerical valuation parameters matching user card entities indicators tracking frameworks arrays", tags = {"Credit Card Engine"})
    public ResponseEntity<?> getCreditCardScore(@PathVariable String cardId) {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        // Check if the user exists and if they own the credit card
        if (user != null && isCreditCardOwnedByUser (user, cardId)) {
            // Retrieve the credit score
            Optional<Integer> creditScore = creditScoreService.getCreditScore(cardId);

            // Check if the credit score is present
            if (creditScore.isPresent()) {
                return new ResponseEntity<>(creditScore.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Credit Card not found", HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }

    // New endpoint to update credit card score
    @PutMapping("/score/{cardId}")
    @Operation(summary = "Update numeric parameters indicator metric values records validation mapping structural configurations settings", tags = {"Credit Card Engine"})
    public ResponseEntity<?> updateCreditCardScore(@PathVariable String cardId, @RequestParam int newCreditScore) {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);

        if (user != null && isCreditCardOwnedByUser (user, cardId)) {
            Optional<CreditCard> updatedCreditCard = creditCardService.updateCreditScore(cardId, newCreditScore);
            if (updatedCreditCard.isPresent()) {
                return new ResponseEntity<>(updatedCreditCard.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Credit Card not found", HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/lock/{cardId}")
    @Operation(summary = "Lock card profile context flags", description = "Enforces block variables preventing core state mutations from parsing authorization data arrays mappings checks constraints.", tags = {"Credit Card Engine"})
    public ResponseEntity<CreditCard> lockCreditCard(@PathVariable String cardId) {
        Optional<CreditCard> lockedCard = creditCardService.lockCreditCard(cardId);
        return lockedCard.map(card -> new ResponseEntity<>(card, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/unlock/{cardId}")
    @Operation(summary = "Restore full authorization flags context settings configurations arrays data structural parameters model records", tags = {"Credit Card Engine"})
    public ResponseEntity<CreditCard> unlockCreditCard(@PathVariable String cardId) {
        Optional<CreditCard> unlockedCard = creditCardService.unlockCreditCard(cardId);
        return unlockedCard.map(card -> new ResponseEntity<>(card, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private boolean isCreditCardOwnedByUser(User user, String creditCardId) {
        if (user != null) {
            return user.getCustomers()
                    .stream()
                    .flatMap(customer -> customer.getCreditCardIds().stream())
                    .anyMatch(id -> id.equals(creditCardId));
        }
        return false;
    }
}