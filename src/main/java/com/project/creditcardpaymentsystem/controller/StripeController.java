package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.entity.ProductRequest;
import com.project.creditcardpaymentsystem.entity.StripeResponse;
import com.project.creditcardpaymentsystem.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment Gateway Operations", description = "Bridges platform transaction endpoints securely over externalized Stripe session token management channels pipelines data.")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/checkout")
    @Operation(summary = "Generate live external Stripe payment gateway initialization parameter parameters collections structures maps context rules values models metrics", tags = {"Payment Gateway Operations"})
    public ResponseEntity<StripeResponse> checkoutProducts(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transactional parameters entry payload configuration properties structure model definitions datasets.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequest.class), examples = @ExampleObject(value = "{\n  \"name\": \"Premium Membership Fee Tier\",\n  \"amount\": 2500,\n  \"quantity\": 1,\n  \"currency\": \"usd\"\n}"))
            )
            @RequestBody ProductRequest productRequest) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
        return new ResponseEntity<>(stripeResponse,HttpStatus.OK);
    }
}