package com.project.creditcardpaymentsystem.controller;

import com.project.creditcardpaymentsystem.service.GeminiIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@Tag(name = "Generative Core & Assistant APIs", description = "Proxy services translating structural user natural language prompts safely into remote Google Gemini API components.")
public class AIController {

    @Autowired
    private GeminiIntegrationService geminiIntegrationService;

    @PostMapping("/generate-content")
    @Operation(
            summary = "Query content generation models mapping configurations pipeline parameters context vectors framework structures",
            description = "Feeds parameters text configurations models packets directly into remote Gemini LLM components endpoints structures interfaces.",
            tags = {"Generative Core & Assistant APIs"}
    )
    public ResponseEntity<String> generateContent(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Plain text string matching analysis target configurations requirements structures context specifications mapping conditions logs entries.",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = "Explain the benefits of maintaining a credit utilization ratio below 30% for financial score validation."))
            )
            @RequestBody String prompt) {
        String generatedContent = geminiIntegrationService.generateContent(prompt);
        return new ResponseEntity<>(generatedContent, HttpStatus.OK);
    }
}