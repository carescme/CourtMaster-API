package com.courtmaster.api.controller;

import com.courtmaster.api.dto.LoginRequest;
import com.courtmaster.api.dto.AuthResponse;
import com.courtmaster.api.service.AuthService;
import com.courtmaster.api.dto.RegistroRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    
    //POST

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody RegistroRequest dto) {
        authService.registrarUsuarioPublico(dto);
        return ResponseEntity.ok("Usuario registrado correctamente en CourtMaster. ¡Ya puedes iniciar sesión!");
    }
}