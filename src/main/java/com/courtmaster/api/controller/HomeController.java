package com.courtmaster.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, String> bienvenido() {
        return Map.of(
            "app", "CourtMaster API",
            "status", "Running",
            "version", "1.0.0-SNAPSHOT",
            "mensaje", "Bienvenido al backend de CourtMaster. Los endpoints base están en /api/."
        );
    }
    
}