package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/faqs")
@Tag(name = "Generative Core & Assistant APIs")
public class FAQController {

    @Autowired
    private FAQService faqService;

    @GetMapping
    @Operation(
            summary = "Fetch response configurations data matching internal static FAQ storage maps criteria profiles logs settings",
            description = "Validates question patterns across memory datasets dictionaries to pull hardcoded static knowledge management strings answers records.",
            tags = {"Generative Core & Assistant APIs"}
    )
    public ResponseEntity<String> getFAQ(
            @Parameter(description = "Target question mapping search queries phrase string (e.g., 'What is a credit score?')", required = true)
            @RequestParam String question) {
        String response = faqService.getFAQResponse(question);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}